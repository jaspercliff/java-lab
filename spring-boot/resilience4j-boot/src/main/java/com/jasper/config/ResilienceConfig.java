package com.jasper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class ResilienceConfig {

    @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService timeLimiterScheduler() {
        return Executors.newScheduledThreadPool(2);
    }
}