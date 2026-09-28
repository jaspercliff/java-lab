package com.jasper.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "config1")
// 该注解本身支持nacos配置自动刷新
@Component
@Data
public class ConfigurationPropertiesConfig {
    private String name;
    private String age;
    private String sex;
}
