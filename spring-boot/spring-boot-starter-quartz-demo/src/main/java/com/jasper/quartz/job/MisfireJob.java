package com.jasper.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * 用于演示 misfire 的 Job。
 *
 * <p>观察重点：当发生 misfire 后，{@code scheduledFireTime}（原计划时间）会明显早于
 * {@code fireTime}（实际执行时间）。如果两者几乎相同，说明是正常触发。
 *
 * <p>演示入口见 {@code QuartzJobService#scheduleMisfireDemo(int)}：
 * 先创建一个 5 秒后触发的 Trigger 并立即暂停，等待超过 misfireThreshold 后恢复，
 * Quartz 会调用 {@code Trigger.updateAfterMisfire(...)} 并按 IGNORE 策略补执行一次。
 * 此时 scheduledFireTime 保留原计划时间，fireTime 是实际执行时间，差值即延迟。
 */
@Slf4j
public class MisfireJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        long delayMs = context.getFireTime().getTime() - context.getScheduledFireTime().getTime();
        log.warn("""
                        [MisfireJob] job={} 执行
                          scheduledFireTime={} fireTime={} 延迟={}ms
                          refireCount={} misfireInstruction={} note={}""",
                context.getJobDetail().getKey(),
                context.getScheduledFireTime(),
                context.getFireTime(),
                delayMs,
                context.getRefireCount(),
                context.getTrigger().getMisfireInstruction(),
                context.getMergedJobDataMap().getString("note"));
        context.setResult("misfire-demo-executed-delay-" + delayMs + "ms");
    }
}
