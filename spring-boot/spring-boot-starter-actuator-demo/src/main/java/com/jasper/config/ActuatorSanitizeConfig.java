package com.jasper.config;

import org.springframework.boot.actuate.endpoint.SanitizingFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorSanitizeConfig {

    @Bean
    public SanitizingFunction myCustomSanitizingFunction() {
        return (data) -> {
            // 判断配置项的 Key 是否包含指定字段
            if (data.getKey().toLowerCase().contains("customtoken")) {
                // 使用 withValue 替换对应的值
                return data.withValue("🔒[PROTECTED]");
            }
            return data;
        };
    }
}