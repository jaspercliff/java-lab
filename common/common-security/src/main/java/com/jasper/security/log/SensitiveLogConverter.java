package com.jasper.security.log;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.jasper.security.core.SensitiveFieldMatcher;
import com.jasper.security.core.SensitiveTextMasker;

import java.util.List;

/**
 * Logback 日志脱敏 Converter：对格式化后的日志正文做字段级 + 值级脱敏。
 *
 * <p>注册方式（二选一）：
 * <pre>{@code
 * <!-- 全局：替换所有 %msg -->
 * <conversionRule conversionWord="msg"
 *                 converterClass="com.jasper.security.log.SensitiveLogConverter"/>
 *
 * <!-- 按需：单独声明 conversionWord -->
 * <conversionRule conversionWord="sensitiveMsg"
 *                 converterClass="com.jasper.security.log.SensitiveLogConverter"/>
 * <pattern>%d{HH:mm:ss} %-5level %logger - %sensitiveMsg%n</pattern>
 * }</pre>
 *
 * <p>可在 pattern 中用 option 覆盖默认敏感字段，例如
 * {@code %sensitiveMsg{mySecret,innerToken}}；不传则使用
 * {@link SensitiveFieldMatcher#DEFAULT_FIELDS}。
 *
 * <p>脱敏规则由 {@link SensitiveTextMasker} 提供，与 HTTP 访问日志共用同一套实现。
 *
 * <p>能力边界：只处理 {@link ILoggingEvent#getFormattedMessage()} 返回的正文，
 * 不处理异常堆栈（{@code %ex}）、MDC、以及无字段名且无固定格式的自由文本；
 * 需要堆栈脱敏时应另行配置 Layout 或 ThrowableProxy 渲染。
 */
public class SensitiveLogConverter extends MessageConverter {

    private SensitiveTextMasker masker = new SensitiveTextMasker(
            new SensitiveFieldMatcher(SensitiveFieldMatcher.DEFAULT_FIELDS));

    @Override
    public void start() {
        List<String> options = getOptionList();
        if (options != null && !options.isEmpty()) {
            this.masker = new SensitiveTextMasker(new SensitiveFieldMatcher(options));
        }
        super.start();
    }

    @Override
    public String convert(ILoggingEvent event) {
        String message = super.convert(event);
        if (message == null || message.isEmpty()) {
            return message;
        }
        return masker.mask(message);
    }
}
