package com.jasper.web;

import org.springframework.context.support.ResourceBundleMessageSource;

/** 测试用的 MessageSource，加载 starter 自带的两套文案。 */
final class TestMessages {

    private TestMessages() {
    }

    static ResourceBundleMessageSource source() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("i18n/system", "i18n/business");
        source.setDefaultEncoding("UTF-8");
        source.setFallbackToSystemLocale(false);
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }
}
