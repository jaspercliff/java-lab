package com.jasper.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping
    public String test(){
        return "test";
    }

    /**
     * Fallback 是针对单次调用异常的处理；熔断是针对一段时间内调用结果的状态控制
     * circuitBreaker open  reach 要求之后直接进去fallback
     */
    @GetMapping("fallback")
    @CircuitBreaker(name = "test",fallbackMethod = "fallback")
    public String failureWithFallback() {
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "This is a remote exception");
    }

    private String fallback(HttpServerErrorException ex) {
        return "===fallback Recovered HttpServerErrorException: " + ex.getMessage();
    }
}
