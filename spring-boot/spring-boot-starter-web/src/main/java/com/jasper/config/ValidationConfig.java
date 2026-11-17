package com.jasper.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@RequiredArgsConstructor
public class ValidationConfig {

    private final MessageSource messageSource;

    /**
     * validation 配置i18n
     */
    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        // 注解里的 {key} 会走 Spring MessageSource，按 Accept-Language 解析
        factory.setValidationMessageSource(messageSource);
        return factory;
    }
}
