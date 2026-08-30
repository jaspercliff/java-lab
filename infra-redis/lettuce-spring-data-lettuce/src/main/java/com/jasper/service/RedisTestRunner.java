package com.jasper.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@Slf4j
public class RedisTestRunner {

    /**
     * 报no auth 因为密码是在redis.conf中写的 但是 compose-support识别不出来 所以创建的数据源不对报错 Docker Compose 会根据容器创建
     * Service Connection，并且 Connection Details 会优先于普通的 spring.data.redis.* 连接配置 暂时让 Spring Boot 忽略
     * Compose 的 Redis Service Connection。 compose.yml labels: org.springframework.boot.ignore: true
     */
    @Bean
    ApplicationRunner redisTest(StringRedisTemplate stringRedisTemplate) {
        return args -> {
            stringRedisTemplate.opsForValue().set("name", "jasper");

            String name = stringRedisTemplate.opsForValue().get("name");
            log.info("name:{}", name);
        };
    }
}
