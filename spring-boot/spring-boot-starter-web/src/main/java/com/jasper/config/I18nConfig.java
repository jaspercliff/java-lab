package com.jasper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
public class I18nConfig {

    /**
     * AcceptHeaderLocaleResolver 本身就有默认 Locale 的处理逻辑，但默认情况下并不是固定使用简体中文
     *优先根据 HTTP 请求头 Accept-Language 确定 Locale。
     * 如果请求头没有提供语言信息，则使用 Locale.getDefault()，也就是 JVM 的默认 Locale
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver =
                new AcceptHeaderLocaleResolver();

        resolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);

        resolver.setSupportedLocales(List.of(
                Locale.SIMPLIFIED_CHINESE,
                Locale.US
        ));

        return resolver;
    }
}