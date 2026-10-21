package com.jasper.feign;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 把 HTTP API 调用声明成 Java Interface，让远程调用看起来像调用本地方法。
 */
@FeignClient(name = "random-service",url = "http://127.0.0.1:18081",path = "random"
        ,fallbackFactory = RandomServiceClientFallbackFactory.class)
public interface RandomServiceClient {

    @GetMapping("/test")
    String test();

    @RateLimiter(
            name = "random-service",
            fallbackMethod = "rateLimiterFallback"
    )
    @GetMapping("/absolute?rate=50")
    String absolute();


}
