package com.jasper.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jasper
 * @since 2026-08-31 <br>
 */
@Configuration
public class LettuceConfig {

    @Bean(destroyMethod = "shutdown")
    public RedisClient redisClient() {
        RedisURI uri =
                RedisURI.builder()
                        .withHost("localhost")
                        .withPort(6379)
                        .withPassword("passwd".toCharArray())
                        .build();

        return RedisClient.create(uri);
    }

    @Bean(destroyMethod = "close")
    public StatefulRedisConnection<String, String> redisConnection(RedisClient redisClient) {
        return redisClient.connect();
    }

    @Bean
    public RedisCommands<String, String> redisCommands(
            StatefulRedisConnection<String, String> connection) {
        return connection.sync();
    }
}
