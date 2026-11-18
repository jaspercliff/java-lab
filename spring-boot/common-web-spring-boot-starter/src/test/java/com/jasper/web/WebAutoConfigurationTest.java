package com.jasper.web;

import com.jasper.security.config.SecurityAutoConfiguration;
import com.jasper.security.core.SensitiveFieldMatcher;
import com.jasper.web.config.WebAutoConfiguration;
import com.jasper.web.config.WebI18nAutoConfiguration;
import com.jasper.web.config.properties.WebProperties;
import com.jasper.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WebAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    SecurityAutoConfiguration.class,
                    WebI18nAutoConfiguration.class,
                    WebAutoConfiguration.class));

    @Test
    void registersHandlerAndPropertiesByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(GlobalExceptionHandler.class);
            assertThat(context).hasSingleBean(WebProperties.class);
        });
    }

    @Test
    void backsOffWhenUserDefinesOwnHandler() {
        GlobalExceptionHandler custom = new GlobalExceptionHandler(new WebProperties(true, false),
                new SensitiveFieldMatcher(List.of("password")), TestMessages.source());

        contextRunner.withBean(GlobalExceptionHandler.class, () -> custom)
                .run(context -> {
                    assertThat(context).hasSingleBean(GlobalExceptionHandler.class);
                    assertThat(context.getBean(GlobalExceptionHandler.class)).isSameAs(custom);
                });
    }

    @Test
    void canBeDisabledByProperty() {
        contextRunner.withPropertyValues("jasper.web.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(GlobalExceptionHandler.class));
    }

    @Test
    void bindsExposureProperty() {
        contextRunner.withPropertyValues("jasper.web.expose-exception-message=true")
                .run(context -> assertThat(context.getBean(WebProperties.class).exposeExceptionMessage()).isTrue());
    }
}
