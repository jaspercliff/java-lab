package com.jasper.quartz.listener;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;

/**
 * 全局 SchedulerListener：观察调度器生命周期和“任务/触发器元数据变更”。
 *
 * <p>SchedulerListener 方法很多，按用途分四组即可记住：
 * <ol>
 *   <li><b>生命周期</b>：schedulerStarting / schedulerStarted / schedulerInStandbyMode /
 *       schedulerShuttingdown / schedulerShutdown / schedulerError</li>
 *   <li><b>Job 元数据</b>：jobAdded / jobDeleted / jobPaused / jobsPaused / jobResumed / jobsResumed</li>
 *   <li><b>Trigger 元数据</b>：jobScheduled / jobUnscheduled / triggerFinalized /
 *       triggerPaused / triggersPaused / triggerResumed / triggersResumed</li>
 *   <li><b>数据清理</b>：schedulingDataCleared（对应 Scheduler#clear）</li>
 * </ol>
 *
 * <p>生产用途：把“任务被谁删了/暂停了”写审计日志；调度器进入 standby 或 shutdown 时告警；
 * schedulerError 说明 JobStore/数据库出现异常，需要立即处理。
 */
@Slf4j
@Component
public class GlobalSchedulerListener implements SchedulerListener {

    // ------------------------------------------------------------------
    // 1. 生命周期
    // ------------------------------------------------------------------

    @Override
    public void schedulerStarting() {
        log.info("[SchedulerListener] schedulerStarting");
    }

    @Override
    public void schedulerStarted() {
        log.info("[SchedulerListener] schedulerStarted");
    }

    @Override
    public void schedulerInStandbyMode() {
        log.warn("[SchedulerListener] schedulerInStandbyMode（不触发，但线程池仍存活）");
    }

    @Override
    public void schedulerShuttingdown() {
        log.info("[SchedulerListener] schedulerShuttingDown");
    }

    @Override
    public void schedulerShutdown() {
        log.info("[SchedulerListener] schedulerShutdown");
    }

    @Override
    public void schedulerError(String msg, SchedulerException cause) {
        log.error("[SchedulerListener] schedulerError: {}", msg, cause);
    }

    // ------------------------------------------------------------------
    // 2. Job 元数据变更
    // ------------------------------------------------------------------

    @Override
    public void jobAdded(JobDetail jobDetail) {
        log.info("[SchedulerListener] jobAdded {}", jobDetail.getKey());
    }

    @Override
    public void jobDeleted(JobKey jobKey) {
        log.info("[SchedulerListener] jobDeleted {}", jobKey);
    }

    @Override
    public void jobPaused(JobKey jobKey) {
        log.info("[SchedulerListener] jobPaused {}", jobKey);
    }

    @Override
    public void jobsPaused(String jobGroup) {
        log.info("[SchedulerListener] jobsPaused group={}", jobGroup);
    }

    @Override
    public void jobResumed(JobKey jobKey) {
        log.info("[SchedulerListener] jobResumed {}", jobKey);
    }

    @Override
    public void jobsResumed(String jobGroup) {
        log.info("[SchedulerListener] jobsResumed group={}", jobGroup);
    }

    // ------------------------------------------------------------------
    // 3. Trigger 元数据变更
    // ------------------------------------------------------------------

    @Override
    public void jobScheduled(Trigger trigger) {
        log.info("[SchedulerListener] jobScheduled trigger={} job={}",
                trigger.getKey(), trigger.getJobKey());
    }

    @Override
    public void jobUnscheduled(TriggerKey triggerKey) {
        log.info("[SchedulerListener] jobUnscheduled trigger={}", triggerKey);
    }

    @Override
    public void triggerFinalized(Trigger trigger) {
        log.info("[SchedulerListener] triggerFinalized（不会再触发）trigger={}", trigger.getKey());
    }

    @Override
    public void triggerPaused(TriggerKey triggerKey) {
        log.info("[SchedulerListener] triggerPaused {}", triggerKey);
    }

    @Override
    public void triggersPaused(String triggerGroup) {
        log.info("[SchedulerListener] triggersPaused group={}", triggerGroup);
    }

    @Override
    public void triggerResumed(TriggerKey triggerKey) {
        log.info("[SchedulerListener] triggerResumed {}", triggerKey);
    }

    @Override
    public void triggersResumed(String triggerGroup) {
        log.info("[SchedulerListener] triggersResumed group={}", triggerGroup);
    }

    // ------------------------------------------------------------------
    // 4. 数据清理
    // ------------------------------------------------------------------

    @Override
    public void schedulingDataCleared() {
        log.warn("[SchedulerListener] schedulingDataCleared：所有 Job/Trigger 已被清空");
    }
}
