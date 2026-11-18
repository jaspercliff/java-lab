package com.jasper.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasper.web.advice.ApiResultResponseAdvice;
import com.jasper.web.config.properties.ResponseProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/** 成功响应自动包装的自动配置。 */
@AutoConfiguration
@ConditionalOnClass(ObjectMapper.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "jasper.web", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(ResponseProperties.class)
public class WebResponseAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "jasper.web.response", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public ApiResultResponseAdvice apiResultResponseAdvice(ObjectProvider<ObjectMapper> objectMapper,
                                                           ResponseProperties properties) {
        return new ApiResultResponseAdvice(objectMapper, properties);
    }
}
