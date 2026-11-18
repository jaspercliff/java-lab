package com.jasper.web.config;

import com.jasper.security.core.SensitiveFieldMatcher;
import com.jasper.web.config.properties.WebProperties;
import com.jasper.web.exception.GlobalExceptionHandler;
import org.springframework.context.MessageSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * common-web 自动配置：只在 Servlet Web 应用中装配全局异常处理器。
 *
 * <p>注册方式见 {@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports}
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(
        prefix = "jasper.web",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(WebProperties.class)
public class WebAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(GlobalExceptionHandler.class)
    public GlobalExceptionHandler globalExceptionHandler(WebProperties properties,
                                                         SensitiveFieldMatcher sensitiveMatcher,
                                                         MessageSource messageSource) {
        return new GlobalExceptionHandler(properties, sensitiveMatcher, messageSource);
    }
}
