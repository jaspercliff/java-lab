package com.jasper.security.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 纯文本敏感信息脱敏：按「键值对字段名」+「裸值固定格式」两条规则处理。
 *
 * <p>供日志 Converter、HTTP 访问日志等拿不到结构化字段元数据的场景共用。
 * 与 Jackson 序列化脱敏（{@code @Sensitive}）互补，不依赖任何日志框架。
 *
 * <p>脱敏顺序：键值对 → 裸身份证 → 裸手机号。身份证先于手机号，
 * 避免 18 位身份证中形似手机号的 11 位子串被重复脱敏。
 */
public final class SensitiveTextMasker {

    /**
     * 键值对：字段名 + 可选引号 + 分隔符 + 值。
     * 值段排除引号、空白、常见分隔符以及 {@code = ? &}，避免吞掉后续相邻键值对
     * （如 {@code url=https://host?token=abc} 中的 token）。
     */
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile(
            "(?<key>[A-Za-z_][\\w.-]*)[\"']?\\s*[=:]\\s*[\"']?"
                    + "(?<value>[^\"'\\s,;)}\\]=?&]+)[\"']?");

    /** 裸身份证：18 位末位可为数字或 X；前后不紧邻数字。 */
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("(?<!\\d)\\d{17}[0-9Xx](?!\\d)");

    /** 裸手机号：1 开头、第二位 3-9、共 11 位；前后不紧邻数字。 */
    private static final Pattern PHONE_PATTERN = Pattern.compile("(?<!\\d)1[3-9]\\d{9}(?!\\d)");

    private final SensitiveFieldMatcher fieldMatcher;

    public SensitiveTextMasker(SensitiveFieldMatcher fieldMatcher) {
        this.fieldMatcher = fieldMatcher;
    }

    /** 对整段文本脱敏；null / 空串原样返回。 */
    public String mask(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        String masked = maskKeyValuePairs(text);
        masked = maskByValue(masked, ID_CARD_PATTERN, SensitiveType.ID_CARD);
        return maskByValue(masked, PHONE_PATTERN, SensitiveType.PHONE);
    }

    /** 只替换命中敏感字段的值，非敏感字段连同 key 与分隔符原样写回。 */
    private String maskKeyValuePairs(String text) {
        Matcher m = KEY_VALUE_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder(text.length());
        while (m.find()) {
            String value = m.group("value");
            Object masked = fieldMatcher.mask(m.group("key"), value);
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
    private static String maskByValue(String text, Pattern pattern, SensitiveType type) {
        Matcher m = pattern.matcher(text);
        StringBuilder sb = new StringBuilder(text.length());
        while (m.find()) {
            m.appendReplacement(sb, Matcher.quoteReplacement(type.apply(m.group())));
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
