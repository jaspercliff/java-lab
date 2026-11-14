package com.jasper.quartz.listener;

import com.jasper.quartz.dto.ExecutionRecord;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.stereotype.Component;

/**
 * 全局 JobListener：观察 Job 的执行生命周期。
 *
 * <p>三个回调：
 * <ul>
 *   <li>{@link #jobToBeExecuted}：即将执行（此时还没有进入 Job#execute）</li>
 *   <li>{@link #jobExecutionVetoed}：被 TriggerListener 否决，不会执行</li>
 *   <li>{@link #jobWasExecuted}：执行结束，无论成功失败都会回调</li>
 * </ul>
 *
 * <p>注册方式见 {@code QuartzConfig}：通过 {@code SchedulerFactoryBean#setGlobalJobListeners}
 * 注册为全局监听器。也可以按组/按 Key 注册，见 {@code QuartzBootstrap}。
 */
@Slf4j
@Component
public class GlobalJobListener implements JobListener {

    private final ExecutionHistory history;

    public GlobalJobListener(ExecutionHistory history) {
        this.history = history;
    }

    @Override
    public String getName() {
        // Listener 的名字用于 ListenerManager 去重/移除，必须稳定且唯一
        return "globalJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        log.info("[JobListener] 即将执行 job={} trigger={} scheduledFireTime={}",
                context.getJobDetail().getKey(),
                context.getTrigger().getKey(),
                context.getScheduledFireTime());
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        log.warn("[JobListener] 执行被否决 job={} trigger={}",
                context.getJobDetail().getKey(), context.getTrigger().getKey());
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        long duration = context.getJobRunTime();
        boolean success = jobException == null;

        history.add(new ExecutionRecord(
                context.getJobDetail().getKey().toString(),
                context.getTrigger().getKey().toString(),
                context.getFireTime().toInstant(),
                context.getScheduledFireTime().toInstant(),
                duration,
                success,
                success ? null : jobException.getMessage(),
                context.getResult() == null ? null : String.valueOf(context.getResult()),
                context.getRefireCount()));

        if (success) {
            log.info("[JobListener] 执行完成 job={} 耗时={}ms result={}",
                    context.getJobDetail().getKey(), duration, context.getResult());
        } else {
            log.error("[JobListener] 执行失败 job={} 耗时={}ms",
                    context.getJobDetail().getKey(), duration, jobException);
        }
    }
}
