package com.jasper.web.config;

import com.jasper.web.config.properties.I18nProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * 错误文案国际化自动配置。
 *
 * <p>先于 Spring Boot 默认的 {@link MessageSourceAutoConfiguration} 注册，
 * 让业务码（i18n/business）与系统码（i18n/system）各用一份文件。
 * 应用自定义名为 messageSource 的 Bean 即可完全覆盖；
 * 也可通过 {@code jasper.web.i18n.basenames} 调整 basename。
 */
@AutoConfiguration(before = MessageSourceAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "jasper.web", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(I18nProperties.class)
public class WebI18nAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "messageSource")
    public MessageSource messageSource(I18nProperties properties) {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames(properties.basenames().toArray(String[]::new));
        source.setDefaultEncoding(properties.encoding());
        // 不回退到 JVM 系统 Locale：中文环境默认应取基础文件，只有明确请求英文才用 _en
        source.setFallbackToSystemLocale(false);
        // 找不到 key 时直接用 key 作为文案，避免异常处理器再兜一层
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }
}
