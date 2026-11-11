package com.jasper.taskExecutionAndScheduling.scheduling.api.trigger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class CustomTriggerDemo {

    public static void main(String[] args) {

        ThreadPoolTaskScheduler scheduler =
                new ThreadPoolTaskScheduler();

        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("health-check-");
        scheduler.initialize();

        AtomicBoolean healthy = new AtomicBoolean(true);

        HealthCheckTrigger trigger = new HealthCheckTrigger(healthy);

        scheduler.schedule(() -> {

            log.info("开始检查第三方服务");

            boolean result = checkService();

            healthy.set(result);

            log.info("服务状态：{}", result ? "正常" : "异常");

        }, trigger);
    }

    private static boolean checkService() {
        // 模拟健康检查
        return Math.random() > 0.3;
    }
}