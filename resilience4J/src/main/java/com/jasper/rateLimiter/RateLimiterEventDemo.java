package com.jasper.rateLimiter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
public class RateLimiterEventDemo {
    public static void main(String[] args) {
        // 1. 自定义配置
        RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(3)) //刷新周期
                .limitForPeriod(1) // 每个周期的许可数
                .timeoutDuration(Duration.ofMillis(100)) //获取许可的超时等待时间 设为 0 表示不等待，超限直接拒绝 允许等待 100ms
                .build();

        RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.custom()
                .withRegistryStore(new CacheRateLimiterRegistryStore())
                .withRateLimiterConfig(rateLimiterConfig)
                .build();


        rateLimiterRegistry.getEventPublisher()
                .onEntryAdded(entryAddedEvent -> {
                    RateLimiter addedRateLimiter = entryAddedEvent.getAddedEntry();
                    log.info("RateLimiter {} added", addedRateLimiter.getName());
                })
                .onEntryRemoved(entryRemovedEvent -> {
                    RateLimiter removedRateLimiter = entryRemovedEvent.getRemovedEntry();
                    log.info("RateLimiter {} removed", removedRateLimiter.getName());
                });
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("backend");
        rateLimiter.getEventPublisher()
                .onSuccess(event -> {
                    log.info("rate limiter success event ");
                })
                .onFailure(event -> {
                    log.info("rate limiter failure event ");
                });

        Supplier<String> rawServiceCall = () -> {
            // 模拟远程调用
            return "调用成功：数据响应";
        };
        try {
            for (int i = 0; i < 3; i++) {
                String s = rateLimiter.executeSupplier(rawServiceCall);
                System.out.println(s);
            }
        } catch (RequestNotPermitted e) {
            System.out.println("被限流了：系统繁忙，请稍后再试！");
        }

    }
}
