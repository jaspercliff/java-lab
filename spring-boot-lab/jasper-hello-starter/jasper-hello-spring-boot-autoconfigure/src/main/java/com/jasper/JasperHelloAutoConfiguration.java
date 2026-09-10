package com.jasper;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(JasperHelloProperties.class) // 把 JasperHelloProperties 注册为 Bean，并绑定 application.yml 中的配置
@ConditionalOnProperty(
        prefix = "jasper.hello",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false // 不配置就不能使用，必须明确配置 true
)
public class JasperHelloAutoConfiguration {

    @Bean
    // 自动配置提供默认实现，但不应该强行覆盖用户配置
    @ConditionalOnMissingBean // 如果用户自己已经注册了 JasperHelloService，Starter 就不要再注册一个
    public JasperHelloService jasperHelloService(
            JasperHelloProperties properties
    ) {
        return new JasperHelloService(properties);
    }
}