package com.jasper;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class TimeBasedCircuitBreakerDemo {


    public static void main(String[] args) throws InterruptedException {
        // 创建基于时间的断路器配置
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED) // 设置为基于时间
                .slidingWindowSize(5)                     // 窗口大小为 5 秒
                .minimumNumberOfCalls(3)                  // 在 5 秒的窗口内，至少要有 3 次调用才开始计算失败率
                .failureRateThreshold(50)               // 失败率达到 50% 开启断路器
                .waitDurationInOpenState(Duration.ofSeconds(3)) // 开启后等待 3 秒进入半开状态
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        CircuitBreaker circuitBreaker = registry.circuitBreaker("timeBasedService");
        BackendService service = new BackendService();

        System.out.println("=== 模拟基于时间的滑动窗口测试 ===");

        // 连续进行几次失败调用，观察时间窗口内的统计
        for (int i = 1; i <= 4; i++) {
            service.setShouldFail(true);

            try {
                circuitBreaker.executeSupplier(service::doSomething);
            } catch (Exception e) {
                System.out.println("第 " + i + " 次调用失败，当前状态: " + circuitBreaker.getState());
            }
            Thread.sleep(200); // 每次间隔 200 毫秒，全部挤在 5 秒的窗口内
        }

        // 稍等片刻，看状态是否转为 OPEN
        log.info("最终断路器状态: {}", circuitBreaker.getState());
    }
}