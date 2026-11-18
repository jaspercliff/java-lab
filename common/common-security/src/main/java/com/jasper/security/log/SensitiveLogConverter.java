package com.jasper.security.log;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.jasper.security.core.SensitiveFieldMatcher;
import com.jasper.security.core.SensitiveType;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * <p>脱敏顺序：先键值对（覆盖 password/token 等无固定格式的值），再裸身份证、裸手机号。
 * 先处理身份证是因为 18 位身份证中可能包含形如手机号的 11 位子串，避免重复/错误脱敏。
 *
 * <p>能力边界：只处理 {@link ILoggingEvent#getFormattedMessage()} 返回的正文，
 * 不处理异常堆栈（{@code %ex}）、MDC、以及无字段名且无固定格式的自由文本；
 * 日志正文生成前若敏感信息已被 {@code toString()} 二次加工导致格式被破坏，同样无法识别。
 * 需要堆栈脱敏时应另行配置 Layout 或自定义 ThrowableProxy 渲染。
 */
public class SensitiveLogConverter extends MessageConverter {

    /**
     * 键值对：字段名 + 可选引号 + 分隔符 + 值。
     * 值中排除引号、空白、常见分隔符以及 {@code = ? &}，避免吞掉后续相邻键值对
     * （如 {@code url=https://host?token=abc} 中的 token）。
     */
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile(
            "(?<key>[A-Za-z_][\\w.-]*)[\"']?\\s*[=:]\\s*[\"']?"
                    + "(?<value>[^\"'\\s,;)}\\]=?&]+)[\"']?");

    /** 裸身份证：18 位，末位可为数字或 X；前后不紧邻数字。 */
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("(?<!\\d)\\d{17}[0-9Xx](?!\\d)");

    /** 裸手机号：1 开头、第二位 3-9、共 11 位；前后不紧邻数字。 */
    private static final Pattern PHONE_PATTERN = Pattern.compile("(?<!\\d)1[3-9]\\d{9}(?!\\d)");

    private SensitiveFieldMatcher matcher = new SensitiveFieldMatcher(SensitiveFieldMatcher.DEFAULT_FIELDS);

    @Override
    public void start() {
        List<String> options = getOptionList();
        if (options != null && !options.isEmpty()) {
            this.matcher = new SensitiveFieldMatcher(options);
        }
        super.start();
    }

    @Override
    public String convert(ILoggingEvent event) {
        String message = super.convert(event);
        if (message == null || message.isEmpty()) {
            return message;
        }
        String masked = maskKeyValuePairs(message, matcher);
        masked = maskByValue(masked, ID_CARD_PATTERN, SensitiveType.ID_CARD);
        return maskByValue(masked, PHONE_PATTERN, SensitiveType.PHONE);
    }

    /** 只替换命中敏感字段的值，非敏感字段连同 key 与分隔符原样写回。 */
    private static String maskKeyValuePairs(String message, SensitiveFieldMatcher matcher) {
        Matcher m = KEY_VALUE_PATTERN.matcher(message);
        StringBuilder sb = new StringBuilder(message.length());
        while (m.find()) {
            String value = m.group("value");
            Object masked = matcher.mask(m.group("key"), value);
            String full = m.group();
            String replacement = full.substring(0, m.start("value") - m.start())
                    + masked
                    + full.substring(m.end("value") - m.start());
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /** 按固定格式识别裸值并用对应策略脱敏。 */
    private static String maskByValue(String message, Pattern pattern, SensitiveType type) {
        Matcher m = pattern.matcher(message);
        StringBuilder sb = new StringBuilder(message.length());
        while (m.find()) {
            m.appendReplacement(sb, Matcher.quoteReplacement(type.apply(m.group())));
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
