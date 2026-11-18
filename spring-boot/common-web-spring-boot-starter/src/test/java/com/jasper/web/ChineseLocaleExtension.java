package com.jasper.web;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/** 让测试以中文 Locale 运行，避免测试机默认 Locale 为英文导致断言到英文文案。 */
public class ChineseLocaleExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        LocaleContextHolder.resetLocaleContext();
    }
}
