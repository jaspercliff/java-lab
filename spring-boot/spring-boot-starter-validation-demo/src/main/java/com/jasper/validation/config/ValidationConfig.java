package com.jasper.validation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.HashMap;
import java.util.Map;

/**
 * 知识点：定制 ValidatorFactory——
 *
 * <ul>
 *   <li>fail-fast：默认收集全部错误，设 {@code hibernate.validator.fail_fast=true} 后遇到第一个错就停；
 *       由 {@code validation.fail-fast} 配置开关；</li>
 *   <li>i18n：把 Spring 的 {@link MessageSource} 挂到 Validator，注解里的 {@code {key}}
 *       就能从 messages*.properties 里按 Accept-Language 取不同语言文案。</li>
 * </ul>
 */
@Configuration
public class ValidationConfig {

    @Bean
    public LocalValidatorFactoryBean validator(MessageSource messageSource,
                                               @Value("${validation.fail-fast:false}") boolean failFast) {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        // mac default en-US
        factory.setValidationMessageSource(messageSource);

        Map<String, String> props = new HashMap<>();
        // Hibernate Validator 特有的开关；Jakarta Validation 规范没有统一属性名
        props.put("hibernate.validator.fail_fast", String.valueOf(failFast));
        factory.setValidationPropertyMap(props);
        return factory;
    }
}
