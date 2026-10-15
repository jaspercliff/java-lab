package com.jasper;

import com.jasper.exception.BusinessException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class DefaultDemo {
    public static void main(String[] args) {
        CircuitBreakerRegistry circuitBreakerRegistry =
                CircuitBreakerRegistry.ofDefaults();
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("backendA");

        CircuitBreakerConfig defaultConfig = circuitBreakerRegistry.getDefaultConfig();
        CircuitBreakerConfig overrideConfig = CircuitBreakerConfig.from(defaultConfig)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .build();

//        registry根据名称查找和复用实例。
//        统一管理注册表中的熔断器。
//        支持查询实例、监听注册表事件等扩展能力

        // 如果你不想使用CircuitBreakerRegistry来创建断路器的实例，可以直接创建断路器实例。
        // 创建断路器的自定义配置
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .recordExceptions(IOException.class, TimeoutException.class)
                .ignoreExceptions(BusinessException.class)
                .build();
//        这里调用两次 CircuitBreaker.of()，会创建两个独立的熔断器实例
        CircuitBreaker cb1 = CircuitBreaker
                .of("testName", circuitBreakerConfig);

        CircuitBreaker cb2 = CircuitBreaker
                .of("testName", circuitBreakerConfig);
    }
}
