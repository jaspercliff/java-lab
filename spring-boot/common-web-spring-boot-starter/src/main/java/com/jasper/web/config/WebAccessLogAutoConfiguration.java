package com.jasper.web.config;

import com.jasper.security.core.SensitiveFieldMatcher;
import com.jasper.web.config.properties.AccessLogProperties;
import com.jasper.web.filter.AccessLogFilter;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.util.EnumSet;

/**
 * 访问日志过滤器自动配置。
 *
 * <p>记录 method、target（path variable 与 query 均脱敏）、请求体、状态码、响应体、
 * 耗时与 traceId；请求/响应体记录可通过 {@code jasper.web.access-log.include-request-body}
 * 与 {@code include-response-body} 关闭，长度由 {@code max-body-length} 限制。
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "jasper.web", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(AccessLogProperties.class)
public class WebAccessLogAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "jasper.web.access-log", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public AccessLogFilter accessLogFilter(AccessLogProperties properties,
                                           SensitiveFieldMatcher sensitiveMatcher) {
        return new AccessLogFilter(properties, sensitiveMatcher);
    }

    @Bean
    @ConditionalOnProperty(prefix = "jasper.web.access-log", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<AccessLogFilter> accessLogFilterRegistration(AccessLogFilter filter) {
        FilterRegistrationBean<AccessLogFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setName("accessLogFilter");
        registration.addUrlPatterns("/*");
        // trace starter 的过滤器 order 是 MIN_VALUE+100，排在后面才能拿到 MDC 中的 traceId
        registration.setOrder(Integer.MIN_VALUE + 200);
        registration.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC));
        return registration;
    }
}
