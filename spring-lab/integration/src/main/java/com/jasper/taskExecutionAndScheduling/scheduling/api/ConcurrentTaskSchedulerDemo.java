package com.jasper.taskExecutionAndScheduling.scheduling.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public class ConcurrentTaskSchedulerDemo {
    public static void main(String[] args) {
        ScheduledExecutorService executor =
                Executors.newSingleThreadScheduledExecutor();

        TaskScheduler scheduler = new ConcurrentTaskScheduler(executor);
        ScheduledFuture<?> fixedRateFuture =
                scheduler.scheduleAtFixedRate(
                        () -> log.info("fixedRate 定时任务执行"),
                        Duration.ofSeconds(2)
                );
    }
}
