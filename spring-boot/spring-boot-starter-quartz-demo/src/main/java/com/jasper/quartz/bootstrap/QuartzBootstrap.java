package com.jasper.quartz.bootstrap;

import com.jasper.quartz.config.QuartzDemoProperties;
import com.jasper.quartz.job.*;
import com.jasper.quartz.listener.GroupJobListener;
import com.jasper.quartz.listener.SpecificTriggerListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.KeyMatcher;
import org.quartz.impl.matchers.OrMatcher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

/**
 * 启动时注册一批演示任务。
 * <p>ApplicationRunner see also {@link com.jasper.ApplicationRunnerDemo}</p>
 * <p>为什么用 {@link ApplicationRunner} 而不是 {@code @PostConstruct}？
 * <ul>
 *   <li>此时 Spring 容器已完全就绪，Scheduler、Listener、DataSource 都已初始化；</li>
 *   <li>可以安全地调用 {@code Scheduler} API；</li>
 *   <li>失败时可以抛出异常让应用启动失败（任务配置错误应尽早暴露）。</li>
 * </ul>
 *
 * <p>幂等性：JDBC JobStore 下应用重启后 Job/Trigger 仍然存在，因此这里用
 * {@code checkExists} 做“存在则跳过”，避免重复注册；需要更新时使用 REST API。
 * RAMJobStore 每次启动都是空的，会重新注册。
 */
@SuppressWarnings("JavadocReference")
@Slf4j
@Component
@RequiredArgsConstructor
public class QuartzBootstrap implements ApplicationRunner {

    private final Scheduler scheduler;
    private final QuartzDemoProperties properties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!properties.bootstrap()) {
            log.info("quartz-demo.bootstrap=false，跳过演示任务注册");
            return;
        }

        String group = properties.group();
        log.info("开始注册 Quartz 演示任务，group={}，schedulerStarted={}", group, scheduler.isStarted());

        // 先注册 Matcher Listener，保证后续 jobScheduled 等事件也能被观察
        registerMatcherListeners(group);

//        scheduleHelloJob(group);
        scheduleCounterJob(group);
        scheduleSlowJob(group);
        scheduleFailureJob(group);
        scheduleSpringBeanJob(group);
        scheduleDurableJob(group);
//        scheduleDurableNotJob(group); 启动直接报错 Jobs added with no trigger must be durable.
        scheduleCalendarJob(group);

        log.info("演示任务注册完成：group={}，jobCount={}，打开 http://localhost:8090/api/quartz/jobs 查看",
                group, scheduler.getJobKeys(GroupMatcher.jobGroupEquals(group)).size());
    }

    /**
     * 演示按分组、按指定 Key 注册 Listener（与全局 Listener 互补）。
     */
    private void registerMatcherListeners(String group) throws SchedulerException {
        ListenerManager listenerManager = scheduler.getListenerManager();

        boolean hasGroupListener = listenerManager.getJobListeners().stream()
                .anyMatch(listener -> "groupJobListener".equals(listener.getName()));
        if (!hasGroupListener) {
            // 只监听 demo 组的 Job
            listenerManager.addJobListener(new GroupJobListener(),
                    GroupMatcher.jobGroupEquals(group));
        }

        boolean hasSpecificListener = listenerManager.getTriggerListeners().stream()
                .anyMatch(listener -> "specificTriggerListener".equals(listener.getName()));
        if (!hasSpecificListener) {
            // 只监听两个指定 Trigger：KeyMatcher 精确匹配 + OrMatcher 组合
            OrMatcher<TriggerKey> matcher = OrMatcher.or(
                    KeyMatcher.keyEquals(TriggerKey.triggerKey("helloJob-trigger", group)),
                    KeyMatcher.keyEquals(TriggerKey.triggerKey("counterJob-trigger", group)));
            listenerManager.addTriggerListener(new SpecificTriggerListener(), matcher);
        }
    }

    /**
     * 基础示例：JobDataMap + Trigger 数据覆盖 + 每 10 秒执行。
     */
    private void scheduleHelloJob(String group) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(HelloWorldJob.class)
                .withIdentity("helloJob", group)
                .withDescription("基础 Job：JobDataMap、fireTime、result")
                .usingJobData("message", "Hello from JobDetail")
                .usingJobData("jobLevelNumber", 42)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("helloJob-trigger", group)
                .forJob(job)
                .withDescription("每 10 秒执行一次")
                .withPriority(5)
                // Trigger 上的同名 key 会覆盖 JobDetail 上的值
                .usingJobData("message", "Hello from Trigger（覆盖 JobDetail 同名 key）")
                .usingJobData("triggerMessage", "trigger-only-data")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(10)
                        .repeatForever()
                        // 错过的次数不补跑，从下一个计划点继续
                        .withMisfireHandlingInstructionNextWithRemainingCount())
                .build();

        schedule(job, trigger);
    }

    /**
     * 演示失败后立即重试，以及 requestRecovery。
     */
    private void scheduleFailureJob(String group) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(FailureJob.class)
                .withIdentity("failureJob", group)
                .withDescription("前 2 次失败并 refireImmediately，第 3 次成功")
                .usingJobData("failTimes", 2)
                .usingJobData("businessFailTimes", 3) // 模拟业务执行失败的次数3 > 最大重试次数2 会删除trigger
                // JDBC + 集群模式下，节点崩溃后该 Job 会被其他节点恢复执行
                .requestRecovery(true)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("failureJob-trigger", group)
                .forJob(job)
                .withDescription("每 30 秒执行一次")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(30)
                        .repeatForever()
                        .withMisfireHandlingInstructionNextWithRemainingCount())
                .build();

        schedule(job, trigger);
    }
    /**
     * 演示 @DisallowConcurrentExecution + @PersistJobDataAfterExecution。
     */
    private void scheduleCounterJob(String group) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(CounterJob.class)
                .withIdentity("counterJob", group)
                .withDescription("串行执行并把 count 写回 JobDataMap")
                // 初始 count；JDBC 模式下后续执行会基于持久化值累加
                .usingJobData("count", 0)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("counterJob-trigger", group)
                .forJob(job)
                .withDescription("每 15 秒累加一次")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(15)
                        .repeatForever()
                        .withMisfireHandlingInstructionNextWithRemainingCount())
                .build();

        schedule(job, trigger);
    }

    /**
     * 演示可中断的长任务：15 秒后开始执行，执行 20 秒。
     *
     * <p>可以用 {@code POST /api/quartz/jobs/demo/slowJob/interrupt} 中断。
     */
    private void scheduleSlowJob(String group) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(SlowJob.class)
                .withIdentity("slowJob", group)
                .withDescription("长任务，支持 Scheduler#interrupt")
                .usingJobData("seconds", 20)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("slowJob-trigger", group)
                .forJob(job)
                .withDescription("每 60 秒执行一次，每次约 20 秒")
                // 延迟 15 秒开始，避免应用刚启动就占住线程
                .startAt(Date.from(Instant.now().plusSeconds(15)))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(60)
                        .repeatForever()
                        .withMisfireHandlingInstructionNextWithRemainingCount())
                .build();

        schedule(job, trigger);
    }


    /**
     * 演示 Spring Bean 注入：Job 的构造器参数由 Spring 提供。
     */
    private void scheduleSpringBeanJob(String group) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(SpringBeanJob.class)
                .withIdentity("springBeanJob", group)
                .withDescription("演示 Quartz Job 中使用 Spring Bean")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("springBeanJob-trigger", group)
                .forJob(job)
                .withDescription("每 20 秒执行一次")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(20)
                        .repeatForever()
                        .withMisfireHandlingInstructionNextWithRemainingCount())
                .build();

        schedule(job, trigger);
    }

    /**
     * 演示 durable Job：只注册 JobDetail，不绑定 Trigger。
     *
     * <p>手动触发：
     * {@code POST /api/quartz/jobs/demo/durableJob/trigger}
     */
    private void scheduleDurableJob(String group) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey("durableJob", group);
        if (scheduler.checkExists(jobKey)) {
            log.info("durableJob 已存在，跳过注册");
            return;
        }

        JobDetail job = JobBuilder.newJob(DurableJob.class)
                .withIdentity(jobKey)
                .withDescription("没有 Trigger，只能手动触发")
                .usingJobData("note", "durable job 没有 trigger 也不会被删除")
                .storeDurably()
                .build();

        scheduler.addJob(job, false);
        log.info("durableJob 已注册（无 Trigger），可通过 trigger 接口手动执行");
    }
    private void scheduleDurableNotJob(String group) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey("durableNotJob", group);

        JobDetail job = JobBuilder.newJob(DurableNotJob.class)
                .withIdentity(jobKey)
                .withDescription("没有 Trigger")
                .usingJobData("note", "durable not job 没有 trigger 会被删除")
                .build();

        scheduler.addJob(job, false);
    }
    /**
     * 演示 Calendar 排除节假日：每天 09:00 执行，节假日跳过。
     */
    private void scheduleCalendarJob(String group) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(CalendarJob.class)
                .withIdentity("calendarJob", group)
                .withDescription("每 20 秒执行一次，节假日由 cnHolidays 排除")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("calendarJob-trigger", group)
                .forJob(job)
                .withDescription("每 20 秒执行一次，排除节假日")
                .modifiedByCalendar(properties.holidayCalendar())
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("0/20 * * * * ?")
                                .withMisfireHandlingInstructionDoNothing()
                )
                .build();

        schedule(job, trigger);
    }

    /**
     * 幂等注册：Trigger 存在则跳过；Job 已存在则只补 Trigger。
     *
     * <p>JDBC 模式下不要用 {@code scheduleJob(job, trigger)} 覆盖已存在的 Job，
     * 否则可能把 {@code @PersistJobDataAfterExecution} 持久化的 count 重置掉。
     * Job 已存在时保留原有 JobDataMap，避免覆盖运行时状态。 @PersistJobDataAfterExecution  避免运行数据重制 count
     */
    private void schedule(JobDetail job, Trigger trigger)
            throws SchedulerException {

        JobKey jobKey = job.getKey();
        TriggerKey triggerKey = trigger.getKey();

        boolean jobExists = scheduler.checkExists(jobKey);
        boolean triggerExists = scheduler.checkExists(triggerKey);

        // 1. Job 和 Trigger 都不存在：一次性注册
        if (!jobExists && !triggerExists) {
            scheduler.scheduleJob(job, trigger);
            log.info("Job 和 Trigger 已注册: {} -> {}", jobKey, triggerKey);
            return;
        }

        // 2. Job 已存在：更新 JobDetail
        if (jobExists) {
            JobDetail oldJob = scheduler.getJobDetail(jobKey);

            JobDetail updatedJob = JobBuilder.newJob(job.getJobClass())
                    .withIdentity(jobKey)
                    .withDescription(job.getDescription())
                    .usingJobData(oldJob.getJobDataMap())
                    .storeDurably(job.isDurable())
                    .build();

            scheduler.addJob(updatedJob, true,true);

            log.info("Job 已更新: {}", jobKey);

        } else {
            // Job 不存在，但 Trigger 已存在
            throw new SchedulerException(
                    "Trigger 已存在，但关联 Job 不存在: " + triggerKey);
        }

        // 3. 更新或补充 Trigger
        if (triggerExists) {
            scheduler.rescheduleJob(triggerKey, trigger);
            log.info("Trigger 已更新: {}", triggerKey);
        } else {
            scheduler.scheduleJob(trigger);
            log.info("Trigger 已补充: {}", triggerKey);
        }
    }
}
