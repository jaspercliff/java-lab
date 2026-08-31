package com.jasper.config;

import org.springframework.context.annotation.Configuration;

/**
 * @author jasper
 * @since 2026-08-30 <br>
 */
@Configuration
public class RedisConfig {

    // @Bean
    // public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory)
    // {
    //     RedisTemplate<String, String> template = new RedisTemplate<>();
    //
    //     template.setConnectionFactory(connectionFactory);
    //
    //     template.setKeySerializer(new StringRedisSerializer());
    //     template.setValueSerializer(new StringRedisSerializer());
    //
    //     template.setHashKeySerializer(new StringRedisSerializer());
    //     template.setHashValueSerializer(new StringRedisSerializer());
    //
    //     template.afterPropertiesSet();
    //
    //     return template;
    // }
}
