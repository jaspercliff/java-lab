package com.jasper.web.config;

import com.jasper.web.exception.downStream.FeignExceptionHandler;
import org.springframework.context.MessageSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

/** Feign 异常归一化：只有引入 openfeign 时才装配。 */
@AutoConfiguration
@ConditionalOnClass(name = "feign.FeignException")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "jasper.web", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FeignExceptionAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FeignExceptionHandler feignExceptionHandler(MessageSource messageSource) {
        return new FeignExceptionHandler(messageSource);
    }
}
