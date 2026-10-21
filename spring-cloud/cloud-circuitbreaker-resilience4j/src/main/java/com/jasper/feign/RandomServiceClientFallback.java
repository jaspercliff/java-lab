package com.jasper.feign;

import org.springframework.stereotype.Component;

/**
 * 降级实现类（必须被 Spring 容器管理)
 */
@Component
public class RandomServiceClientFallback implements RandomServiceClient {

    @Override
    public String test() {
        return "RandomServiceClientFallback: test() method fallback executed.";
    }

    @Override
    public String absolute() {
        return "RandomServiceClientFallback: absolute() method fallback executed.";
    }
}
