package com.jasper.quartz.listener;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

/**
 * 按“指定 TriggerKey 集合”注册的 TriggerListener，演示 {@code KeyMatcher + OrMatcher}。
 *
 * <p>注册代码见 {@code QuartzBootstrap#registerMatcherListeners}：
 * <pre>{@code
 * OrMatcher<TriggerKey> matcher = OrMatcher.or(
 *     KeyMatcher.keyEquals(TriggerKey.triggerKey("helloJob-trigger", "demo")),
 *     KeyMatcher.keyEquals(TriggerKey.triggerKey("counterJob-trigger", "demo")));
 * scheduler.getListenerManager().addTriggerListener(new SpecificTriggerListener(), matcher);
 * }</pre>
 *
 * <p>适用场景：只对核心任务做额外审计/告警，避免全局 Listener 日志过多。
 */
@Slf4j
public class SpecificTriggerListener implements TriggerListener {

    @Override
    public String getName() {
        return "specificTriggerListener";
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        log.debug("[SpecificTriggerListener] 指定触发器触发 {}", trigger.getKey());
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        // 这里不干预执行，返回 false
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        log.warn("[SpecificTriggerListener] 指定触发器 misfire {}", trigger.getKey());
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context,
                                Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        log.debug("[SpecificTriggerListener] 指定触发器完成 {} instruction={}",
                trigger.getKey(), triggerInstructionCode);
    }
}
