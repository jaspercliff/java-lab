package com.jasper.controller;

import com.jasper.feign.RandomServiceClient;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("random")
@RequiredArgsConstructor
public class RandomController {
    private final RandomServiceClient randomServiceClient;

    @GetMapping("/test")
    public String test() {
        return randomServiceClient.test();
    }

    @RateLimiter(
            name = "random-service",
            fallbackMethod = "rateLimiterFallback"
    )
    @GetMapping("/absolute")
    public String absolute() {
        return randomServiceClient.absolute();
    }

    public String rateLimiterFallback(RequestNotPermitted ex) {
        return "RateLimiter fallback: too many requests";
    }
}
