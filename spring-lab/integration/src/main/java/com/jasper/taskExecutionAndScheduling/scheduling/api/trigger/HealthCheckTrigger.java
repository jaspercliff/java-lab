package com.jasper.taskExecutionAndScheduling.scheduling.api.trigger;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.time.Instant;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

public class HealthCheckTrigger implements Trigger {

    private final AtomicBoolean healthy;

    public HealthCheckTrigger(AtomicBoolean healthy) {
        this.healthy = healthy;
    }

    @Override
    public Instant nextExecution(TriggerContext context) {

        Instant lastCompletion = context.lastCompletion();

        // 第一次立即执行
        if (lastCompletion == null) {
            return Instant.now();
        }

        // 正常：30 秒后检查
        // 异常：5 秒后检查
        Duration delay = healthy.get()
                ? Duration.ofSeconds(1)
                : Duration.ofSeconds(5);

        return lastCompletion.plus(delay);
    }
}