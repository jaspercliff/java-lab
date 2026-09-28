package com.jasper.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@RefreshScope // value 注解必须配合该注解支持动态刷新
public class ValueAnnoConfig {

    @Value("${config.name}")
    private String name;

    @Value("${config.age}")
    private String age;

    /**
     * nacos 和配置文件都有时，nacos 配置文件优先级高于配置文件
     */
    @Value("${config.hobby}")
    private String hobby;
}
