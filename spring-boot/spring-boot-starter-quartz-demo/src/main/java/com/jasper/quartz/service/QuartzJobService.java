package com.jasper.quartz.service;

import com.jasper.quartz.config.QuartzDemoProperties;
import com.jasper.quartz.dto.*;
import com.jasper.quartz.job.MisfireJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Quartz 调度核心服务：把官方 API 按“增删改查 + 启停 + 预览”封装成可复用的方法。
 *
 * <p>本类覆盖的实用知识点：
 * <ul>
 *   <li>JobKey / TriggerKey / GroupMatcher 的查询；</li>
 *   <li>JobBuilder、TriggerBuilder、四种 ScheduleBuilder；</li>
 *   <li>repeatCount、startAt/endAt、priority、calendar、requestRecovery、durable；</li>
 *   <li>misfire 策略按 Trigger 类型翻译；</li>
 *   <li>scheduleJob / addJob / rescheduleJob / unscheduleJob / deleteJob；</li>
 *   <li>pause/resume Job 与 Trigger、triggerJob 手动触发、interrupt 中断；</li>
 *   <li>getTriggersOfJob、getTriggerState、getCurrentlyExecutingJobs、getMetaData；</li>
 *   <li>CronExpression 预览下一次触发时间。</li>
 * </ul>
 *
 * <p>重要原则：<b>不要在 Job 内直接调用本 Service 去操作同一个 Scheduler 的元数据</b>，
 * 容易造成死锁或状态不一致；Job 内只做业务，调度管理交给管理端/启动器。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuartzJobService {

    private final Scheduler scheduler;
    private final QuartzDemoProperties properties;

    /**
     * 调度器元数据。
     */
    public SchedulerInfo schedulerInfo() throws SchedulerException {
        SchedulerMetaData meta = scheduler.getMetaData();
        return new SchedulerInfo(
                meta.getSchedulerName(),
                meta.getSchedulerInstanceId(),
                meta.getSchedulerClass().getName(),
                meta.getJobStoreClass().getName(),
                meta.isJobStoreClustered(),
                meta.isJobStoreSupportsPersistence(),
                meta.getThreadPoolClass().getName(),
                meta.getThreadPoolSize(),
                meta.getNumberOfJobsExecuted(),
                meta.getRunningSince() == null ? null : meta.getRunningSince().toInstant(),
                meta.isStarted(),
                meta.isInStandbyMode(), // 待机，暂停触发任务
                meta.isShutdown(),
                meta.getVersion(),
                scheduler.getCalendarNames(),
                scheduler.getJobGroupNames(),
                scheduler.getTriggerGroupNames());
    }

    // ==================================================================
    // 查询
    // ==================================================================

    /**
     * 列出所有分组下的所有 Job。
     *
     * <p>{@code getJobGroupNames()} + {@code getJobKeys(GroupMatcher)} 是遍历 Job 的标准写法。
     * 不要用 {@code getJobKeys(GroupMatcher.anyJobGroup())} 后直接忽略分组——
     * 分组是运维隔离任务的重要手段。
     */
    public List<JobView> listJobs() throws SchedulerException {
        List<JobView> result = new ArrayList<>();
        for (String group : scheduler.getJobGroupNames()) {
            for (JobKey key : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(group))) {
                result.add(toView(key));
            }
        }
        result.sort(Comparator.comparing(JobView::group).thenComparing(JobView::name));
        return result;
    }

    /**
     * 查询单个 Job 及其所有 Trigger。
     */
    public JobView getJob(String group, String name) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(name, group);
        if (!scheduler.checkExists(jobKey)) {
            throw new NoSuchElementException("Job 不存在: " + jobKey);
        }
        return toView(jobKey);
    }

    /**
     * 预览 Cron 表达式接下来的触发时间。
     *
     * <p>这是上线前验证 Cron 的最低成本方式，尤其适合检查：
     * 时区是否符合预期、是否跨月、是否包含 31 号、是否在周末触发。
     */
    public CronPreview previewCron(String expression, String timeZone, int count) {
        if (!StringUtils.hasText(expression)) {
            throw new IllegalArgumentException("expression 不能为空");
        }
        int safeCount = Math.clamp(count, 1, 20);
        try {
            CronExpression cron = new CronExpression(expression);
            TimeZone zone = resolveTimeZone(timeZone);
            cron.setTimeZone(zone);

            List<ZonedDateTime> nextFireTimes = new ArrayList<>();
            //  每次都要以上一次触发时间作为下一次查询的起点
            Date cursor = new Date();
            for (int i = 0; i < safeCount; i++) {
                // 返回严格晚于指定时间的下一次匹配时间
                Date next = cron.getNextValidTimeAfter(cursor);
                if (next == null) {
                    break;
                }
                nextFireTimes.add(next.toInstant().atZone(zone.toZoneId()));
                cursor = next;
            }
            return new CronPreview(expression, zone.getID(), nextFireTimes);
        } catch (ParseException ex) {
            throw new IllegalArgumentException("非法 Cron 表达式: " + expression, ex);
        }
    }

    /**
     * 当前正在执行的 Job（仅本 Scheduler 实例）。
     */
    public List<ExecutingJobView> currentlyExecuting() throws SchedulerException {
        return scheduler.getCurrentlyExecutingJobs().stream()
                .map(context -> new ExecutingJobView(
                        context.getJobDetail().getKey().toString(),
                        context.getTrigger().getKey().toString(),
                        context.getFireInstanceId(),
                        context.getFireTime().toInstant()))
                .toList();
    }


    // ==================================================================
    // 创建 / 修改 / 删除
    // ==================================================================

    /**
     * 创建并调度一个 Job。
     *
     * <p>流程：
     * <ol>
     *   <li>解析 Job 类，构造 JobDetail（JobDataMap、durable、requestRecovery）；</li>
     *   <li>triggerType=NONE 时只 addJob（要求 durable）；</li>
     *   <li>否则构造 Trigger，并处理已存在 Key 的 replace 语义；</li>
     *   <li>返回最新视图。</li>
     * </ol>
     *
     * <p>注意：{@code scheduler.scheduleJob(jobDetail, trigger)} 在 Job 已存在时会抛
     * {@code ObjectAlreadyExistsException}，所以这里显式区分“Job 存在/Trigger 存在”。
     */
    public JobView createJob(JobRequest request) throws SchedulerException {
        if (request == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }
        String name = requireText(request.name(), "name");
        String group = defaultGroup(request.group());
        JobKey jobKey = JobKey.jobKey(name, group);
        Class<? extends Job> jobClass = resolveJobClass(request.jobClass());

        JobBuilder jobBuilder = JobBuilder.newJob(jobClass)
                .withIdentity(jobKey)
                .requestRecovery(Boolean.TRUE.equals(request.requestRecovery()));// 集群节点宕机

        if (StringUtils.hasText(request.description())) {
            jobBuilder.withDescription(request.description());
        }
        if (request.data() != null && !request.data().isEmpty()) {
            JobDataMap jobDataMap = new JobDataMap();

            request.data().forEach((key, value) -> {
                if (value == null) {
                    throw new IllegalArgumentException(
                            "JobDataMap 不允许 null: " + key
                    );
                }

                if (value instanceof String
                        || value instanceof Number
                        || value instanceof Boolean) {
                    jobDataMap.put(key, String.valueOf(value));
                } else {
                    throw new IllegalArgumentException(
                            "JobDataMap 只允许 String、Number、Boolean: " + key
                    );
                }
            });

            jobBuilder.usingJobData(jobDataMap);
        }

        boolean durable = Boolean.TRUE.equals(request.durable());
        jobBuilder.storeDurably(durable);
        JobDetail jobDetail = jobBuilder.build();

        TriggerType triggerType = request.triggerType() == null ? TriggerType.SIMPLE : request.triggerType();

        // 只注册 JobDetail，不创建 Trigger：必须是 durable，否则 Quartz 会立即把它删掉
        if (triggerType == TriggerType.NONE) {
            if (!durable) {
                throw new IllegalArgumentException("triggerType=NONE 时必须 durable=true，否则 Job 会被 Quartz 删除");
            }
            boolean replace = Boolean.TRUE.equals(request.replace());
            if (scheduler.checkExists(jobKey) && !replace) {
                throw new IllegalArgumentException("Job 已存在: " + jobKey + "（可传 replace=true 覆盖）");
            }
            scheduler.addJob(jobDetail, replace);
            return toView(jobKey);
        }

        Trigger trigger = buildTrigger(request, jobDetail, group, name, triggerType);
        boolean replace = Boolean.TRUE.equals(request.replace());

        if (scheduler.checkExists(jobKey)) {
            if (!replace) {
                throw new IllegalArgumentException("Job 已存在: " + jobKey + "（可传 replace=true 覆盖）");
            }
            // replace=true：先更新 JobDetail，再处理 Trigger
            scheduler.addJob(jobDetail, true);
        }

        if (scheduler.checkExists(trigger.getKey())) {
            if (!replace) {
                throw new IllegalArgumentException("Trigger 已存在: " + trigger.getKey() + "（可传 replace=true 覆盖）");
            }
            // 保留原 TriggerKey，只替换 schedule/数据；返回下一次触发时间
            Date next = scheduler.rescheduleJob(trigger.getKey(), trigger);
            log.info("替换 Trigger {}，nextFireTime={}", trigger.getKey(), next);
        } else if (scheduler.checkExists(jobKey)) {
            // Job 已存在（可能是 durable Job），只挂 Trigger
            scheduler.scheduleJob(trigger);
        } else {
            // 一次性注册 JobDetail + Trigger
            scheduler.scheduleJob(jobDetail, trigger);
        }

        return toView(jobKey);
    }

    /**
     * 修改已存在 Trigger 的 Cron 表达式。
     *
     * <p>{@code rescheduleJob} 会保留 TriggerKey，但会用新 Trigger 替换旧的；
     * 因此这里手动复制旧 Trigger 的 priority、JobDataMap、start/end、calendar，
     * 避免“只改 Cron，其他配置全丢”的常见事故。
     */
    public JobView rescheduleCron(String group, String name, CronUpdateRequest request) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(name, group);
        if (!scheduler.checkExists(jobKey)) {
            throw new NoSuchElementException("Job 不存在: " + jobKey);
        }
        if (request == null || !StringUtils.hasText(request.cron())) {
            throw new IllegalArgumentException("cron 不能为空");
        }
        validateCron(request.cron());

        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
        if (triggers.isEmpty()) {
            throw new IllegalStateException("Job 没有 Trigger，无法 reschedule；请先创建 Trigger 或使用 triggerJob 手动执行");
        }
        Trigger oldTrigger = triggers.get(0);
        TriggerKey triggerKey = oldTrigger.getKey();

        MisfirePolicy policy = request.misfirePolicy() == null ? MisfirePolicy.SMART : request.misfirePolicy();
        CronScheduleBuilder schedule = CronScheduleBuilder.cronSchedule(request.cron())
                .inTimeZone(resolveTimeZone(request.timeZone()));
        applyCronMisfire(schedule, policy);

        TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .forJob(jobKey)
                .withPriority(oldTrigger.getPriority())
                // 复制旧 Trigger 上的数据，避免丢失
                .usingJobData(new JobDataMap(oldTrigger.getJobDataMap()));
        // 注意：withSchedule 的返回值是 TriggerBuilder<CronTrigger>，
        // 不要在这里链式赋值给 TriggerBuilder<Trigger>，拆成独立语句最清晰。
        builder.withSchedule(schedule);

        if (oldTrigger.getStartTime() != null) {
            builder.startAt(oldTrigger.getStartTime());
        }
        if (oldTrigger.getEndTime() != null) {
            builder.endAt(oldTrigger.getEndTime());
        }
        if (StringUtils.hasText(oldTrigger.getCalendarName())) {
            builder.modifiedByCalendar(oldTrigger.getCalendarName());
        }

        Date next = scheduler.rescheduleJob(triggerKey, builder.build());
        if (next == null) {
            throw new IllegalStateException("reschedule 失败，Trigger 不存在: " + triggerKey);
        }
        log.info("Cron 已更新 trigger={} expression={} nextFireTime={}", triggerKey, request.cron(), next);
        return toView(jobKey);
    }

    /**
     * 删除 Job 及其所有 Trigger。
     *
     * <p>返回 false 表示 Job 本来就不存在；这里转成 404 语义。
     */
    public void deleteJob(String group, String name) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(name, group);
        if (!scheduler.deleteJob(jobKey)) {
            throw new NoSuchElementException("Job 不存在: " + jobKey);
        }
    }

    // ==================================================================
    // 暂停 / 恢复 / 手动触发 / 中断
    // pauseJob是暂停这个 Job 关联的所有 Trigger
    // ==================================================================

    public void pauseJob(String group, String name) throws SchedulerException {
        JobKey jobKey = requireJob(group, name);
        scheduler.pauseJob(jobKey);
    }

    public void resumeJob(String group, String name) throws SchedulerException {
        JobKey jobKey = requireJob(group, name);
        scheduler.resumeJob(jobKey);
    }

    public void pauseTrigger(String group, String name) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(name, group);
        if (!scheduler.checkExists(triggerKey)) {
            throw new NoSuchElementException("Trigger 不存在: " + triggerKey);
        }
        scheduler.pauseTrigger(triggerKey);
    }

    public void resumeTrigger(String group, String name) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(name, group);
        if (!scheduler.checkExists(triggerKey)) {
            throw new NoSuchElementException("Trigger 不存在: " + triggerKey);
        }
        // 如果 Trigger 在暂停期间错过了触发时间，resume 时 Quartz 会按 misfire 策略处理
        scheduler.resumeTrigger(triggerKey);
    }

    /**
     * 立即触发一次，不影响原有 Trigger 计划。
     *
     * <p>{@code data} 只在本次执行中可见，会与 JobDetail 的 JobDataMap 合并（本次优先）。
     * 常用于“手动补数据”“运维重跑”。
     */
    public void triggerNow(String group, String name, Map<String, Object> data) throws SchedulerException {
        JobKey jobKey = requireJob(group, name);
        JobDataMap jobDataMap = new JobDataMap(data == null ? Map.of() : data);
        scheduler.triggerJob(jobKey, jobDataMap);
    }

    /**
     * 中断正在执行的 Job。
     *
     * @return true 表示至少中断了一个正在执行的实例；false 表示当前没有实例在跑
     */
    public boolean interrupt(String group, String name) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(name, group);
        if (!scheduler.checkExists(jobKey)) {
            throw new NoSuchElementException("Job 不存在: " + jobKey);
        }
        return scheduler.interrupt(jobKey);
    }

    // ==================================================================
    // Misfire 演示
    // ==================================================================

    /**
     * 安排一个“注定 misfire”的任务，用于观察 misfire 策略。
     *
     * <p>步骤：
     * <ol>
     *   <li>创建一次性 SimpleTrigger，{@code startAt = now + delaySeconds}；</li>
     *   <li>立即 {@code pauseTrigger}，让它“该触发时处于暂停状态”；</li>
     *   <li>等待超过 {@code org.quartz.jobStore.misfireThreshold} 后，调用
     *       {@code POST /api/quartz/triggers/demo/misfireJob-trigger/resume}；</li>
     *   <li>Quartz 在 resume 时检测到 nextFireTime 已过期，调用
     *       {@code Trigger.updateAfterMisfire(...)}，按 IGNORE 策略补执行一次；
     *       此时 {@code scheduledFireTime} 仍是原计划时间，{@code fireTime} 是实际执行时间，
     *       两者之差就是“迟到了多久”。</li>
     * </ol>
     *
     * <p>为什么这里用 IGNORE 而不是 FIRE_NOW？
     * FIRE_NOW 会把 nextFireTime 直接改成“现在”，Job 里看到的 scheduledFireTime 也变成现在，
     * 不方便观察延迟；IGNORE 保留原计划时间，更适合教学演示。生产环境请按业务语义选择。
     *
     * <p>本 Demo 把 misfireThreshold 配成 5 秒（application.yml），生产默认是 60 秒。
     */
    public JobView scheduleMisfireDemo(int delaySeconds) throws SchedulerException {
        String group = properties.group();
        JobKey jobKey = JobKey.jobKey("misfireJob", group);

        // 清理上一次演示，保证可重复执行
        scheduler.deleteJob(jobKey);

        JobDetail job = JobBuilder.newJob(MisfireJob.class)
                .withIdentity(jobKey)
                .withDescription("misfire 演示：暂停后恢复，观察补触发")
                .usingJobData("note", "暂停超过 misfireThreshold 后恢复")
                // durable：一次性 Trigger 完成后 Job 仍保留，方便继续观察
                .storeDurably()
                .build();

        // 表示当前时间往后推 delaySeconds 秒，保证 nextFireTime 在 misfireThreshold 之前
        Date startAt = DateBuilder.futureDate(Math.max(1, delaySeconds), DateBuilder.IntervalUnit.SECOND);
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("misfireJob-trigger", group)
                .forJob(job)
                .startAt(startAt)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        // 只触发一次；misfire 时保留原计划时间并补执行
                        .withRepeatCount(0)
                        .withMisfireHandlingInstructionIgnoreMisfires())
                .build();

        scheduler.scheduleJob(job, trigger);
        // 关键：立即暂停，让 nextFireTime 到点时不会被正常消费
        scheduler.pauseTrigger(trigger.getKey());
        log.info("misfire 演示已就绪：job={} startAt={}，请等待 {} 秒后调用 resume",
                jobKey, startAt, delaySeconds);
        return toView(jobKey);
    }

    // ==================================================================
    // 内部辅助
    // ==================================================================

    /**
     * 根据请求构造 Trigger。
     *
     * <p>这里是“四种 Trigger + misfire 策略”最集中的地方，建议对照 Quartz 官方
     * ScheduleBuilder 的 Javadoc 阅读。
     */
    private Trigger buildTrigger(JobRequest request, JobDetail jobDetail,
                                 String group, String name, TriggerType triggerType) {
        String triggerName = StringUtils.hasText(request.triggerName())
                ? request.triggerName()
                : name + "-trigger";
        String triggerGroup = StringUtils.hasText(request.triggerGroup())
                ? request.triggerGroup()
                : group;

        TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, triggerGroup)
                .forJob(jobDetail)
                .withPriority(request.priority() == null ? 5 : request.priority());

        if (StringUtils.hasText(request.description())) {
            builder.withDescription(request.description());
        }
        if (StringUtils.hasText(request.calendar())) {
            // 绑定 Calendar：被排除的日期/时段不会触发
            builder.modifiedByCalendar(request.calendar());
        }

        // startAt 不传则 startNow；注意 startNow 只是“从现在开始计算”，不是“立刻执行”
        if (StringUtils.hasText(request.startAt())) {
            builder.startAt(parseInstant(request.startAt(), "startAt"));
        } else {
            builder.startNow();
        }
        if (StringUtils.hasText(request.endAt())) {
            builder.endAt(Date.from(parseInstant(request.endAt(), "endAt")));
        }

        int intervalSeconds = request.intervalSeconds() == null ? 60 : request.intervalSeconds();
        int repeatCount = request.repeatCount() == null
                ? SimpleTrigger.REPEAT_INDEFINITELY
                : request.repeatCount();
        if (repeatCount < SimpleTrigger.REPEAT_INDEFINITELY) {
            throw new IllegalArgumentException("repeatCount 不能小于 -1（-1 表示永久）");
        }
        MisfirePolicy misfirePolicy = request.misfirePolicy() == null
                ? MisfirePolicy.SMART
                : request.misfirePolicy();

        switch (triggerType) {
            case SIMPLE -> {
                validateInterval(intervalSeconds);
                SimpleScheduleBuilder schedule = SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(intervalSeconds)
                        // -1 = 永久重复；0 = 只执行一次
                        .withRepeatCount(repeatCount);
                applySimpleMisfire(schedule, misfirePolicy);
                builder.withSchedule(schedule);
            }
            case CRON -> {
                validateCron(request.cron());
                CronScheduleBuilder schedule = CronScheduleBuilder.cronSchedule(request.cron())
                        .inTimeZone(resolveTimeZone(request.timeZone()));
                applyCronMisfire(schedule, misfirePolicy);
                builder.withSchedule(schedule);
            }
            case CALENDAR_INTERVAL -> {
                validateInterval(intervalSeconds);
                CalendarIntervalScheduleBuilder schedule =
                        CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
                                .withIntervalInSeconds(intervalSeconds);
                applyCalendarIntervalMisfire(schedule, misfirePolicy);
                builder.withSchedule(schedule);
            }
            case DAILY_TIME_INTERVAL -> {
                validateInterval(intervalSeconds);
                DailyTimeIntervalScheduleBuilder schedule =
                        DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule()
                                // 每天 00:00 - 23:59 之间按间隔执行；可按需改成工作日/9-18 点
                                .onEveryDay()
                                .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(0, 0))
                                .endingDailyAt(TimeOfDay.hourAndMinuteOfDay(23, 59))
                                .withIntervalInSeconds(intervalSeconds);
                applyDailyTimeIntervalMisfire(schedule, misfirePolicy);
                builder.withSchedule(schedule);
            }
            default -> throw new IllegalArgumentException("不支持的 Trigger 类型: " + triggerType);
        }

        return builder.build();
    }

    private void applySimpleMisfire(SimpleScheduleBuilder schedule, MisfirePolicy policy) {
        switch (policy) {
            case SMART -> {
                // 不显式设置，使用 Quartz 的默认智能策略
            }
            case IGNORE -> schedule.withMisfireHandlingInstructionIgnoreMisfires();
            case FIRE_NOW -> schedule.withMisfireHandlingInstructionFireNow();
            case NEXT_WITH_REMAINING_COUNT -> schedule.withMisfireHandlingInstructionNextWithRemainingCount();
            case NEXT_WITH_EXISTING_COUNT -> schedule.withMisfireHandlingInstructionNextWithExistingCount();
            case NOW_WITH_REMAINING_COUNT -> schedule.withMisfireHandlingInstructionNowWithRemainingCount();
            case NOW_WITH_EXISTING_COUNT -> schedule.withMisfireHandlingInstructionNowWithExistingCount();
            case DO_NOTHING -> throw new IllegalArgumentException(
                    "SimpleTrigger 不支持 DO_NOTHING，请使用 NEXT_WITH_* / NOW_WITH_* / FIRE_NOW");
        }
    }

    private void applyCronMisfire(CronScheduleBuilder schedule, MisfirePolicy policy) {
        switch (policy) {
            case SMART -> {
            }
            case IGNORE -> schedule.withMisfireHandlingInstructionIgnoreMisfires();
            case FIRE_NOW -> schedule.withMisfireHandlingInstructionFireAndProceed();
            case DO_NOTHING -> schedule.withMisfireHandlingInstructionDoNothing();
            default -> throw new IllegalArgumentException(
                    "CronTrigger 不支持 " + policy + "，请使用 SMART / IGNORE / FIRE_NOW / DO_NOTHING");
        }
    }

    private void applyCalendarIntervalMisfire(CalendarIntervalScheduleBuilder schedule, MisfirePolicy policy) {
        switch (policy) {
            case SMART -> {
            }
            case IGNORE -> schedule.withMisfireHandlingInstructionIgnoreMisfires();
            case FIRE_NOW -> schedule.withMisfireHandlingInstructionFireAndProceed();
            case DO_NOTHING -> schedule.withMisfireHandlingInstructionDoNothing();
            default -> throw new IllegalArgumentException(
                    "CalendarIntervalTrigger 不支持 " + policy + "，请使用 SMART / IGNORE / FIRE_NOW / DO_NOTHING");
        }
    }

    private void applyDailyTimeIntervalMisfire(DailyTimeIntervalScheduleBuilder schedule, MisfirePolicy policy) {
        switch (policy) {
            case SMART -> {
            }
            case IGNORE -> schedule.withMisfireHandlingInstructionIgnoreMisfires();
            case FIRE_NOW -> schedule.withMisfireHandlingInstructionFireAndProceed();
            case DO_NOTHING -> schedule.withMisfireHandlingInstructionDoNothing();
            default -> throw new IllegalArgumentException(
                    "DailyTimeIntervalTrigger 不支持 " + policy + "，请使用 SMART / IGNORE / FIRE_NOW / DO_NOTHING");
        }
    }

    private JobView toView(JobKey jobKey) throws SchedulerException {
        JobDetail detail = scheduler.getJobDetail(jobKey);
        if (detail == null) {
            throw new NoSuchElementException("Job 不存在: " + jobKey);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        detail.getJobDataMap().forEach((key, value) -> data.put(key, jsonSafe(value)));

        List<TriggerView> triggers = new ArrayList<>();
        for (Trigger trigger : scheduler.getTriggersOfJob(jobKey)) {
            triggers.add(toTriggerView(trigger));
        }

        return new JobView(
                detail.getKey().getName(),
                detail.getKey().getGroup(),
                detail.getDescription(),
                detail.getJobClass().getName(),
                detail.isDurable(),
                detail.isConcurrentExecutionDisallowed(),
                detail.isPersistJobDataAfterExecution(),
                detail.requestsRecovery(),
                data,
                triggers);
    }

    private TriggerView toTriggerView(Trigger trigger) throws SchedulerException {
        String cronExpression = null;
        String timeZone = null;
        Long repeatCount = null;
        Long timesTriggered = null;
        String interval = null;

        if (trigger instanceof CronTrigger cronTrigger) {
            cronExpression = cronTrigger.getCronExpression();
            timeZone = cronTrigger.getTimeZone().getID();
        }
        if (trigger instanceof SimpleTrigger simpleTrigger) {
            repeatCount = (long) simpleTrigger.getRepeatCount();
            timesTriggered = (long) simpleTrigger.getTimesTriggered();
            interval = "every " + simpleTrigger.getRepeatInterval() + "ms";
        }
        if (trigger instanceof CalendarIntervalTrigger calendarIntervalTrigger) {
            interval = "every " + calendarIntervalTrigger.getRepeatInterval()
                    + " " + calendarIntervalTrigger.getRepeatIntervalUnit();
        }
        if (trigger instanceof DailyTimeIntervalTrigger dailyTrigger) {
            interval = "every " + dailyTrigger.getRepeatInterval()
                    + " " + dailyTrigger.getRepeatIntervalUnit()
                    + " between " + dailyTrigger.getStartTimeOfDay()
                    + " and " + dailyTrigger.getEndTimeOfDay()
                    + " days=" + dailyTrigger.getDaysOfWeek();
        }

        return new TriggerView(
                trigger.getKey().getName(),
                trigger.getKey().getGroup(),
                trigger.getClass().getSimpleName(),
                scheduler.getTriggerState(trigger.getKey()).name(),
                toInstant(trigger.getStartTime()),
                toInstant(trigger.getEndTime()),
                toInstant(trigger.getNextFireTime()),
                toInstant(trigger.getPreviousFireTime()),
                trigger.getPriority(),
                trigger.getCalendarName(),
                trigger.getMisfireInstruction(),
                cronExpression,
                timeZone,
                repeatCount,
                timesTriggered,
                interval);
    }

    private JobKey requireJob(String group, String name) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(name, group);
        if (!scheduler.checkExists(jobKey)) {
            throw new NoSuchElementException("Job 不存在: " + jobKey);
        }
        return jobKey;
    }

    private Class<? extends Job> resolveJobClass(String className) {
        String value = requireText(className, "jobClass");
        try {
            Class<?> clazz = Class.forName(value);
            if (!Job.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException("jobClass 必须实现 org.quartz.Job: " + value);
            }
            return clazz.asSubclass(Job.class);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("找不到 Job 类: " + value, ex);
        }
    }

    private String defaultGroup(String group) {
        return StringUtils.hasText(group) ? group : properties.group();
    }

    private String requireText(String value, String field) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(field + " 不能为空");
        }
        return value;
    }

    private void validateInterval(int intervalSeconds) {
        if (intervalSeconds <= 0) {
            throw new IllegalArgumentException("intervalSeconds 必须大于 0");
        }
    }

    /**
     * 提前校验 Cron，避免 CronScheduleBuilder 抛出难以理解的 RuntimeException，
     * 也保证 REST 层统一返回 400 而不是 500。
     */
    private void validateCron(String cron) {
        if (!StringUtils.hasText(cron)) {
            throw new IllegalArgumentException("cron 不能为空");
        }
        if (!CronExpression.isValidExpression(cron)) {
            throw new IllegalArgumentException("非法 Cron 表达式: " + cron);
        }
    }

    private Instant parseInstant(String value, String field) {
        try {
            return Instant.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(
                    field + " 必须是 ISO-8601 Instant，例如 2026-11-11T13:00:00Z，实际值: " + value, ex);
        }
    }

    private TimeZone resolveTimeZone(String timeZone) {
        String id = StringUtils.hasText(timeZone) ? timeZone : properties.timeZone();
        try {
            return TimeZone.getTimeZone(ZoneId.of(id));
        } catch (DateTimeException ex) {
            throw new IllegalArgumentException("非法时区: " + id, ex);
        }
    }

    private Instant toInstant(Date date) {
        return date == null ? null : date.toInstant();
    }

    /**
     * JobDataMap 里可能放任意对象；返回 JSON 前把非标量值转成字符串，避免序列化失败。
     */
    private Object jsonSafe(Object value) {
        if (value == null
                || value instanceof String
                || value instanceof Number
                || value instanceof Boolean) {
            return value;
        }
        return String.valueOf(value);
    }
}
