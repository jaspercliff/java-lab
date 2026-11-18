package com.jasper.security.config;

import com.jasper.security.core.SensitiveFieldMatcher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/** security 自动配置，注册共享的敏感字段匹配器。 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "jasper.security", name = "enabled",
        havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({SecurityProperties.class, SensitiveProperties.class})
public class SecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SensitiveFieldMatcher sensitiveFieldMatcher(SensitiveProperties properties) {
        return new SensitiveFieldMatcher(properties.fields());
    }
}
