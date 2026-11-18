package com.jasper.web.support;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/** 按当前请求 Locale 解析错误文案的小工具，key 不存在时用默认文案兜底。 */
public final class I18nMessages {

    private I18nMessages() {
    }

    public static String get(MessageSource source, String key, String defaultMessage) {
        return source.getMessage(key, null, defaultMessage, LocaleContextHolder.getLocale());
    }
}


