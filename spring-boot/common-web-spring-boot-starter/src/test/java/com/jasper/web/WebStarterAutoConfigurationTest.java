package com.jasper.web;

import com.jasper.security.config.SecurityAutoConfiguration;
import com.jasper.security.core.SensitiveFieldMatcher;
import com.jasper.web.advice.ApiResultResponseAdvice;
import com.jasper.web.config.DownstreamExceptionAutoConfiguration;
import com.jasper.web.config.FeignExceptionAutoConfiguration;
import com.jasper.web.config.JacksonAutoConfiguration;
import com.jasper.web.config.JacksonCustomizer;
import com.jasper.web.config.WebAccessLogAutoConfiguration;
import com.jasper.web.config.WebAutoConfiguration;
import com.jasper.web.config.WebI18nAutoConfiguration;
import com.jasper.web.config.WebResponseAutoConfiguration;
import com.jasper.web.exception.downStream.DownstreamExceptionHandler;
import com.jasper.web.exception.downStream.FeignExceptionHandler;
import com.jasper.web.exception.GlobalExceptionHandler;
import com.jasper.web.filter.AccessLogFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.MessageSource;

import static org.assertj.core.api.Assertions.assertThat;

class WebStarterAutoConfigurationTest {

    private final WebApplicationContextRunner runner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    SecurityAutoConfiguration.class,
                    WebI18nAutoConfiguration.class,
                    WebAutoConfiguration.class,
                    WebResponseAutoConfiguration.class,
                    WebAccessLogAutoConfiguration.class,
                    JacksonAutoConfiguration.class,
                    DownstreamExceptionAutoConfiguration.class,
                    FeignExceptionAutoConfiguration.class));

    @Test
    void registersAllCapabilitiesByDefault() {
        runner.run(context -> {
            assertThat(context).hasSingleBean(MessageSource.class);
            assertThat(context).hasSingleBean(SensitiveFieldMatcher.class);
            assertThat(context).hasSingleBean(GlobalExceptionHandler.class);
            assertThat(context).hasSingleBean(ApiResultResponseAdvice.class);
            assertThat(context).hasSingleBean(AccessLogFilter.class);
            assertThat(context).hasSingleBean(JacksonCustomizer.class);
            assertThat(context).hasSingleBean(DownstreamExceptionHandler.class);
            assertThat(context).hasSingleBean(FeignExceptionHandler.class);
        });
    }

    @Test
    void globalSwitchDisablesEverything() {
        runner.withPropertyValues("jasper.web.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(GlobalExceptionHandler.class);
                    assertThat(context).doesNotHaveBean(ApiResultResponseAdvice.class);
                    assertThat(context).doesNotHaveBean(AccessLogFilter.class);
                    assertThat(context).doesNotHaveBean(JacksonCustomizer.class);
                    assertThat(context).doesNotHaveBean(DownstreamExceptionHandler.class);
                    assertThat(context).doesNotHaveBean(FeignExceptionHandler.class);
                });
    }

    @Test
    void accessLogAndResponseCanBeDisabledIndependently() {
        runner.withPropertyValues("jasper.web.access-log.enabled=false", "jasper.web.response.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(AccessLogFilter.class);
                    assertThat(context).doesNotHaveBean(ApiResultResponseAdvice.class);
                    assertThat(context).hasSingleBean(GlobalExceptionHandler.class);
                });
    }
}
