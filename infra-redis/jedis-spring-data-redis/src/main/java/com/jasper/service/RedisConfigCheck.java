package com.jasper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisConfigCheck {

    private final RedisProperties redisProperties;

    @jakarta.annotation.PostConstruct
    public void check() {
        log.info(
                "redis host={}, port={}, username={}, password={}",
                redisProperties.getHost(),
                redisProperties.getPort(),
                redisProperties.getUsername(),
                redisProperties.getPassword()
        );
    }
}