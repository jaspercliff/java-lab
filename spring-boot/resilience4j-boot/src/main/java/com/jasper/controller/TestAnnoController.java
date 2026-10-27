package com.jasper.controller;

import com.jasper.exchange.ProducerRandomClient;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("test")
@Slf4j
@RequiredArgsConstructor
public class TestAnnoController {

    //for test
    private final AtomicInteger counter = new AtomicInteger();
    private final ProducerRandomClient randomClient;

    /**
     * Fallback 是针对单次调用异常的处理；熔断是针对一段时间内调用结果的状态控制
     * circuitBreaker open  reach 要求之后直接进去fallback <br>
     * see also cloud-circuitbreaker-resilience4j {@link com.jasper.controller.RandomController}
     */
    @GetMapping("circuitBreaker")
    @CircuitBreaker(name = "test",fallbackMethod = "fallback")
    public String failureWithFallback() {
        String absolute = randomClient.absolute();
        return "absolute:" + absolute;
    }

    private String fallback(HttpServerErrorException.InternalServerError ex) {
        return "===fallback Recovered HttpServerErrorException: " + ex.getMessage();
    }
    private String fallback(CallNotPermittedException ex) {
        return "===fallback Recovered CallNotPermittedException: " + ex.getMessage();
    }
    @GetMapping("rateLimiter")
//    permits 一次消耗几个permit 例如导出设置为5  invalid
    @RateLimiter(name = "test", fallbackMethod = "fallback")
    public String rateLimiter() {
        return "rateLimiter";
    }

    private String fallback(RequestNotPermitted permitted) {
        return "===fallback rateLimiter RequestNotPermitted: " + permitted.toString();
    }

    @GetMapping("retry")
    @Retry(name = "test", fallbackMethod = "fallback")
    public String retry() throws IOException {
        int attempt = counter.incrementAndGet();
        log.info("查询库存，第 {} 次调用", attempt);
        if (attempt < 3) {
            throw new IOException("库存服务暂时不可用");
        }
        if (attempt == 3) {
            counter.set(0); // 重置计数器，以便下次调用时重新开始计数
        }
        return "库存查询成功";
    }

    private String fallback(IOException e) {
        log.error("库存查询失败，已达到最大重试次数", e);
        return "库存服务暂时不可用，请稍后重试";
    }

    @GetMapping("timeLimiter")
    @TimeLimiter(name = "test",fallbackMethod = "fallback")
    public CompletableFuture<String> timeLimiter() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "enough stock";
        });

    }

    private CompletableFuture<String> fallback(TimeoutException e) {
        return CompletableFuture.completedFuture("库存查询超时");
    }

    @GetMapping("bulkhead/{param}")
    @Bulkhead(name = "test", fallbackMethod = "fallback")
    public String doSomething(@PathVariable String param) {
        log.info("===doSomething param: {}", param);
        // 核心业务逻辑（仍然运行在 Tomcat 线程上，但同时最多只有 5 个线程能进来）
        return "Success: " + param;
    }

    private String fallback(BulkheadFullException ex) {
        log.error("触发并发隔离降级：当前访问人数过多，请稍后再试。");
        return "触发并发隔离降级：当前访问人数过多，请稍后再试。";
    }
}
