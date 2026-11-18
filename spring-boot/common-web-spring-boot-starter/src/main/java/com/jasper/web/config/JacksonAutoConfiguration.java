package com.jasper.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasper.web.config.properties.JacksonProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/** Jackson 全局约定自动配置。 */
@AutoConfiguration
@ConditionalOnClass({ObjectMapper.class, Jackson2ObjectMapperBuilder.class})
@ConditionalOnProperty(prefix = "jasper.web", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(JacksonProperties.class)
public class JacksonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JacksonCustomizer jacksonCustomizer(JacksonProperties properties) {
        return new JacksonCustomizer(properties);
    }
}
