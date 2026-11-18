package com.jasper.web.config;

import com.jasper.web.exception.downStream.DownstreamExceptionHandler;
import org.springframework.context.MessageSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

/** RestClient / RestTemplate 下游异常归一化的自动配置。 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "jasper.web", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DownstreamExceptionAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DownstreamExceptionHandler downstreamExceptionHandler(MessageSource messageSource) {
        return new DownstreamExceptionHandler(messageSource);
    }
}
