package com.jasper.controller;

import com.jasper.exchange.ProducerRandomClient;
import io.github.resilience4j.bulkhead.*;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;
import java.util.function.Supplier;

@RestController
@RequestMapping("testDeco")
@Slf4j
public class TestDecoController {
    private static final String BACKEND_B = "backendB";
    private final ProducerRandomClient randomClient;
    private final CircuitBreaker circuitBreaker;
    private final RateLimiter rateLimiter;
    private final Retry retry;

    private final TimeLimiter timeLimiter;
    @Resource
    private  ScheduledExecutorService timeLimiterScheduler;

    // 信号量隔离
    private final Bulkhead bulkhead;
    // 线程池隔离
    private final ThreadPoolBulkhead threadPoolBulkhead;

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
    }

    private <T> T execute(Supplier<T> supplier){
        return Decorators.ofSupplier(supplier)
                .withCircuitBreaker(circuitBreaker)
                .withBulkhead(bulkhead)
                .withRetry(retry)
                .get();
    }
    private String fallback(Throwable throwable) {

        Throwable cause = unwrap(throwable);

        if (cause instanceof CallNotPermittedException) {
            return "===fallback：熔断器已打开，拒绝调用";
        }

        if (cause instanceof RequestNotPermitted) {
            return "===fallback：rateLimiter已打开，拒绝调用";
        }

        if (cause instanceof TimeoutException) {
            return "===fallback：timeLimiter调用超时";
        }
        if (cause instanceof BulkheadFullException) {
            return "===fallback：bulkhead已满，拒绝调用";
        }

        return "===fallback：下游调用失败，异常类型："
                + cause.getClass().getSimpleName()
                + "，原因：" + cause.getMessage();
    }
// 异常包装 拆开
    private Throwable unwrap(Throwable throwable) {
        while ((throwable instanceof CompletionException
                || throwable instanceof ExecutionException)
                && throwable.getCause() != null) {

            throwable = throwable.getCause();
        }

        return throwable;
    }
    @GetMapping("circuitBreaker")
    public String circuitBreaker() {
        return Decorators.ofSupplier(randomClient::absolute)
                .withCircuitBreaker(circuitBreaker)
                .withFallback(this::fallback)
                .get();
    }

    @GetMapping("rateLimiter")
    public String rateLimiter() {
        return Decorators.ofSupplier(randomClient::absolute)
                .withRateLimiter(rateLimiter)
                .withFallback(this::fallback)
                .get();
    }

    /**
     * 先尝试重试，重试仍然失败，再由熔断器根据失败情况决定是否打开
     */
    @GetMapping("retry")
    public String retry(){
        return Decorators.ofSupplier(randomClient::absolute)
                .withRetry(retry)
                .withCircuitBreaker(circuitBreaker)
                .withFallback(this::fallback)
                .get();
    }

    private String timeout(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            log.info(e.getMessage());
        }
        return "";
    }
    @GetMapping("timeLimiter")
    public String timeLimiter() {
        return Decorators.ofCompletionStage(()->CompletableFuture.supplyAsync(this::timeout))
                .withTimeLimiter(timeLimiter, timeLimiterScheduler)
                .withFallback(this::fallback)
                .get()
                .toCompletableFuture()
                .join();
    }

    @GetMapping("bulkhead")
    public String bulkhead() {
        log.info("===bulkhead:当前线程池活跃线程数：{}", bulkhead.getMetrics().getAvailableConcurrentCalls());
        String s = Decorators.ofSupplier(randomClient::absolute)
                .withBulkhead(bulkhead)
                .withFallback(this::fallback)
                .get();
        log.info("===bulkhead:响应内容：{}", s);
        return s;
    }

    @GetMapping("threadPoolBulkhead")
    public String threadPoolBulkhead() {
        log.info("===threadPoolBulkhead:当前线程池活跃线程数：{}", threadPoolBulkhead.getMetrics().getActiveThreadCount());
        String s = Decorators.ofSupplier(randomClient::absolute)
                .withThreadPoolBulkhead(threadPoolBulkhead)
                .withFallback(this::fallback)
                .get()
                .toCompletableFuture()
                .join();
        log.info("===threadPoolBulkhead:响应内容：{}", s);
        return s;
    }
}
