package com.jasper.controller;

import com.jasper.exchange.ProducerRandomClient;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

@RestController
@RequestMapping("testDeco")
@Slf4j
public class TestDecoController {
    private static final String BACKEND_B = "backendB";
    private final ProducerRandomClient randomClient;
    private final CircuitBreaker circuitBreaker;
    private final Bulkhead bulkhead;
    private final ThreadPoolBulkhead threadPoolBulkhead;
    private final Retry retry;
    private final RateLimiter rateLimiter;
    private final TimeLimiter timeLimiter;
    private final ScheduledExecutorService scheduledExecutorService;

    public TestDecoController(
            ProducerRandomClient randomClient, CircuitBreakerRegistry circuitBreakerRegistry,
            ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry,
            BulkheadRegistry bulkheadRegistry,
            RetryRegistry retryRegistry,
            RateLimiterRegistry rateLimiterRegistry,
            TimeLimiterRegistry timeLimiterRegistry){
        this.randomClient = randomClient;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(BACKEND_B);
        this.bulkhead = bulkheadRegistry.bulkhead(BACKEND_B);
        this.threadPoolBulkhead = threadPoolBulkheadRegistry.bulkhead(BACKEND_B);
        this.retry = retryRegistry.retry(BACKEND_B);
        this.rateLimiter = rateLimiterRegistry.rateLimiter(BACKEND_B);
        this.timeLimiter = timeLimiterRegistry.timeLimiter(BACKEND_B);
        this.scheduledExecutorService = Executors.newScheduledThreadPool(3);
    }
    private <T> T execute(Supplier<T> supplier){
        return Decorators.ofSupplier(supplier)
                .withCircuitBreaker(circuitBreaker)
                .withBulkhead(bulkhead)
                .withRetry(retry)
                .get();
    }

    @GetMapping("circuitBreaker")
    public String circuitBreaker() {
        return Decorators.ofSupplier(randomClient::absolute)
                .withCircuitBreaker(circuitBreaker)
                .withFallback(throwable -> {
                    if (throwable instanceof CallNotPermittedException) {
                        return "===fallback：熔断器已打开，拒绝调用";
                    }

                    return "===fallback：下游调用失败，异常类型："
                            + throwable.getClass().getSimpleName()
                            + "，原因：" + throwable.getMessage();
                })                .get();
    }


}
