package com.jasper.security;

import com.jasper.security.config.SecurityAutoConfiguration;
import com.jasper.security.core.SensitiveFieldMatcher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityAutoConfigurationTest {

    // 不需要启动完整的 Spring Boot 应用，就可以单独测试某个 @Configuration、@Bean、自动配置类以及条件装配逻辑。
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SecurityAutoConfiguration.class));

    @Test
    void registersMatcherByDefault() {
        runner.run(context -> {
            assertThat(context).hasSingleBean(SensitiveFieldMatcher.class);
            assertThat(context.getBean(SensitiveFieldMatcher.class).mask("password", "123"))
                    .isEqualTo("******");
        });
    }

    @Test
    void canBeDisabled() {
        runner.withPropertyValues("jasper.security.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(SensitiveFieldMatcher.class));
    }

    @Test
    void customFieldsReplaceDefaults() {
        runner.withPropertyValues("jasper.security.sensitive.fields=internalCode")
                .run(context -> {
                    SensitiveFieldMatcher matcher = context.getBean(SensitiveFieldMatcher.class);
                    assertThat(matcher.isSensitive("internalCode")).isTrue();
                    assertThat(matcher.isSensitive("password")).isFalse();
                });
    }

    /**
     * 自己定义的 Bean，应该优先于 Starter 提供的默认 Bean
     */
    @Test
    void userDefinedMatcherWins() {
        SensitiveFieldMatcher custom = new SensitiveFieldMatcher(List.of("custom"));
        runner.withBean(SensitiveFieldMatcher.class, () -> custom)
                .run(context -> {
                    assertThat(context).hasSingleBean(SensitiveFieldMatcher.class);
                    assertThat(context.getBean(SensitiveFieldMatcher.class)).isSameAs(custom);
                });
    }
}
