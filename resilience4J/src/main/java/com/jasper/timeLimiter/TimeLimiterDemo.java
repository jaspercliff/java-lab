package com.jasper.timeLimiter;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Supplier;

@Slf4j
public class TimeLimiterDemo {
    public static void main(String[] args) {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .cancelRunningFuture(true)
                .timeoutDuration(Duration.ofMillis(500))
                .build();

        TimeLimiterRegistry timeLimiterRegistry = TimeLimiterRegistry.of(config);
        TimeLimiter timeLimiter = timeLimiterRegistry.timeLimiter("backend");
// 创建定时任务调度器 需要一个调度器对非阻塞CompletableFuture进行调度，控制超时时间
        ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);
        Supplier<CompletionStage<Integer>> supplier = () -> CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                // nothing
            }
            return 42;
        });

        //  使用 TimeLimiter 限制等待时间
        try {
            Integer result = timeLimiter.executeCompletionStage(scheduler,  supplier).toCompletableFuture().get();

            System.out.println("查询结果：" + result);

        } catch (Exception e) {
            if (e.getCause() instanceof TimeoutException) {
                System.out.println("查询超时");
            } else {
                System.out.println("调用异常：" + e);
            }
        } finally {
            scheduler.shutdownNow();
        }
    }
}
