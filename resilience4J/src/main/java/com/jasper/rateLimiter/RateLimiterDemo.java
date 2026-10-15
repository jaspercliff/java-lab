package com.jasper.rateLimiter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
public class RateLimiterDemo {
    public static void main(String[] args) throws InterruptedException {
        // 1. 自定义配置
        RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(3)) //刷新周期
                .limitForPeriod(1) // 每个周期的许可数
                .timeoutDuration(Duration.ofMillis(100)) //获取许可的超时等待时间 设为 0 表示不等待，超限直接拒绝 允许等待 100ms
                .build();

        RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.of(rateLimiterConfig);
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("backend");

        Supplier<String> rawServiceCall = () -> {
            // 模拟远程调用
            return "调用成功：数据响应";
        };

        execute(rateLimiter,rawServiceCall);
// 等待一个刷新周期，重置令牌桶状态
        log.info("等待 3 秒刷新周期...");
        Thread.sleep(3000);
        log.info("=== 阶段二：动态修改 limitForPeriod = 2 ===");

        //在运行时更改速率限制器参数。新的超时持续时间不会影响当前正在等待权限的线程。
        // 新的限制不会影响当前期间的权限，只会从下一个时间段应用
        rateLimiter.changeLimitForPeriod(2);

        execute(rateLimiter,rawServiceCall);


        log.info("等待 3 秒刷新周期...");
        Thread.sleep(3000);
        log.info("=== 阶段二：动态修改 limitForPeriod = 2 ===");

        execute(rateLimiter,rawServiceCall);

    }

    private static void execute(RateLimiter rateLimiter, Supplier<String> rawServiceCall) throws InterruptedException {
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
