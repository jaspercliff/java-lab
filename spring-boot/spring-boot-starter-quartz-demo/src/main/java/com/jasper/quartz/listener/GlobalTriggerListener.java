package com.jasper.quartz.listener;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.springframework.stereotype.Component;

/**
 * 全局 TriggerListener：观察 Trigger 的触发过程，并演示“否决执行”。
 *
 * <p>五个回调：
 * <ul>
 *   <li>{@link #triggerFired}：Trigger 已触发，Job 即将执行</li>
 *   <li>{@link #vetoJobExecution}：返回 true 可阻止本次 Job 执行（例如：分布式锁没抢到、维护窗口）</li>
 *   <li>{@link #triggerMisfired}：发生 misfire（这是排查“任务为什么没跑/补跑”的关键入口）</li>
 *   <li>{@link #triggerComplete}：Trigger 本次触发完成，同时给出 Quartz 的后续指令</li>
 * </ul>
 *
 * <p>本类通过 JobDataMap 里的 {@code veto=true} 演示否决：
 * 创建任务时传 {@code "data": {"veto": true}}，本次执行会被跳过，
 * {@code GlobalJobListener#jobExecutionVetoed} 会被调用。
 *
 * <p>注意：veto 不应滥用。需要“跳过本次”时，更常见的做法是判断业务状态后直接 return；
 * veto 适合“框架层面不允许执行”的场景（如集群锁、灰度开关）。
 */
@Slf4j
@Component
public class GlobalTriggerListener implements TriggerListener {

    @Override
    public String getName() {
        return "globalTriggerListener";
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        log.info("[TriggerListener] 触发 trigger={} job={} fireTime={}",
                trigger.getKey(), context.getJobDetail().getKey(), context.getFireTime());
    }
// Veto 不是执行失败，而是 Quartz 在执行 Job 之前，决定这一次不执行
    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        Object veto = context.getMergedJobDataMap().get("veto");
        boolean shouldVeto = Boolean.TRUE.equals(veto)
                || "true".equalsIgnoreCase(String.valueOf(veto));
        if (shouldVeto) {
            log.warn("[TriggerListener] JobDataMap.veto=true，否决本次执行 trigger={}", trigger.getKey());
        }
        return shouldVeto;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        log.warn("""
                        [TriggerListener] 发生 misfire trigger={} jobKey={}
                          nextFireTime={} misfireInstruction={}""",
                trigger.getKey(),
                trigger.getJobKey(),
                trigger.getNextFireTime(),
                trigger.getMisfireInstruction());
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context,
                                Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        log.info("[TriggerListener] 触发完成 trigger={} instruction={} nextFireTime={}",
                trigger.getKey(), triggerInstructionCode, trigger.getNextFireTime());
    }
}
