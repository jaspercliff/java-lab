
package com.jasper.taskExecutionAndScheduling.scheduling.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public class ThreadPoolTaskSchedulerDemo {

    public static void main(String[] args) {

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("scheduled-");
        scheduler.initialize();

        // 1. fixedRate：固定频率，每隔 2 秒执行
        ScheduledFuture<?> fixedRateFuture =
                scheduler.scheduleAtFixedRate(
                        () -> log.info("fixedRate 定时任务执行"),
                        Duration.ofSeconds(2)
                );
        // 2. PeriodicTrigger：固定频率，每隔 2 秒执行
        PeriodicTrigger fixedRateTrigger =
                new PeriodicTrigger(Duration.ofSeconds(2));

        fixedRateTrigger.setFixedRate(true);
        ScheduledFuture<?> periodicTriggerFixedRate = scheduler.schedule(
                () -> log.info("PeriodicTrigger fixedRate 执行"),
                fixedRateTrigger
        );

        // 3. fixedDelay：上次执行完成后，等待 3 秒再次执行
        ScheduledFuture<?> fixedDelayFuture =
                scheduler.scheduleWithFixedDelay(
                        () -> log.info("fixedDelay 定时任务执行"),
                        Duration.ofSeconds(3)
                );

        // 4. Cron：每小时执行一次
        ScheduledFuture<?> hourlyFuture =
                scheduler.schedule(
                        () -> log.info("cron 每小时任务执行"),
                        new CronTrigger("@hourly")
                );

        // 5. initialDelay：延迟 1 秒执行一次
        ScheduledFuture<?> initialDelayFuture =
                scheduler.schedule(
                        () -> log.info("initialDelay 任务执行一次"),
                        Instant.now().plusSeconds(1)
                );

        // 6. Cron：每天凌晨 2 点执行
        ScheduledFuture<?> dailyFuture =
                scheduler.schedule(
                        () -> log.info("执行每日数据清理"),
                        new CronTrigger("0 0 2 * * ?")
                );

        // 后续可以通过 ScheduledFuture 取消任务
        // fixedRateFuture.cancel(false);

    }
}
