package com.jasper;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import java.time.Duration;

public class CountBasedCircuitBreakerDemo {
    public static void main(String[] args) throws InterruptedException {
        // Create a custom configuration for a CircuitBreaker
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(4)
                .minimumNumberOfCalls(4)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(3000)) // from open to half open
                //OpenState schedule  tryAcquirePermission manual
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .permittedNumberOfCallsInHalfOpenState(2)
//                .recordExceptions(IOException.class, TimeoutException.class)
//                .ignoreExceptions(BusinessException.class)
                // 1. 定义多长时间算“慢”：比如超过 2 秒的请求就认为是慢调用
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                // 2. 定义慢调用比例阈值：当慢调用占比达到 60% 时触发熔断
                .slowCallRateThreshold(60)
                .build();

// Create a CircuitBreakerRegistry with a custom global configuration
        CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("backendA");

// 初始化后端服务
        BackendService backendService = new BackendService();

// 4. 模拟连续发起 6 次调用
        for (int i = 1; i <= 6; i++) {

            if (i==5){
                Thread.sleep(3500); // make circuit breaker from open to half open
            }
            // 让前 4 次请求故意失败，用来凑齐 minimumNumberOfCalls 并触发断路器开启
            // 第 5、6 次服务其实恢复了
            backendService.setShouldFail(i <= 4);

            try {
                // 使用 Resilience4j 的 executeSupplier 执行并受断路器保护
                String result = circuitBreaker.executeSupplier(backendService::doSomething);
                System.out.println("第 " + i + " 次调用成功: " + result);
            } catch (Exception e) {
                // 捕获异常（包括业务异常、或断路器开启时拒绝调用的 CallNotPermittedException）
                System.out.println("第 " + i + " 次调用异常/被拦截: [" + e.getClass().getSimpleName() + "] " + e.getMessage() + " -> 【走降级恢复逻辑】");
            }

            Thread.sleep(300); // 每次调用间隔 300 毫秒
        }
    }
}
