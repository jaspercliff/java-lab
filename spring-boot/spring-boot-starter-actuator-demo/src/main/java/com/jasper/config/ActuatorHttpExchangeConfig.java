package com.jasper.config;

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorHttpExchangeConfig {

    @Bean
    public HttpExchangeRepository httpExchangeRepository() {
        // 创建一个内存缓冲区，最多记录最近 100 条 HTTP 交互日志
        InMemoryHttpExchangeRepository repository = new InMemoryHttpExchangeRepository();
        repository.setCapacity(100); 
        return repository;
    }
}