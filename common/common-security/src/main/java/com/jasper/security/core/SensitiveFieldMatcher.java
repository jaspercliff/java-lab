package com.jasper.security.core;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 按字段名识别敏感数据并脱敏，供访问日志和校验异常共用。
 *
 * <p>字段名可以是 {@code address.password} 这样的级联路径，只要其中一段命中关键字即可。
 */
public final class SensitiveFieldMatcher {

    public static final String MASK = "******";

    private final List<String> normalizedFields;

    public SensitiveFieldMatcher(Collection<String> fields) {
        this.normalizedFields = fields.stream()
                .filter(Objects::nonNull)
                .map(field -> field.toLowerCase(Locale.ROOT))
                .filter(field -> !field.isBlank())
                .toList();
    }

    /** 字段名包含任意敏感关键字即命中（忽略大小写）。 */
    public boolean isSensitive(String field) {
        if (field == null || field.isBlank()) {
            return false;
        }
        String lower = field.toLowerCase(Locale.ROOT);
        return normalizedFields.stream().anyMatch(lower::contains);
    }

    /** 敏感字段按字段类型脱敏，其余原样返回；null 保持 null。 */
    public Object mask(String field, Object value) {
        if (value == null || !isSensitive(field)) {
            return value;
        }
        return SensitiveType.mask(value.toString(), inferType(field));
    }

    /** 手机号、身份证号保留尾号便于排查，其他字段全遮蔽。 */
    private SensitiveType inferType(String field) {
        String lower = field.toLowerCase(Locale.ROOT);
        if (lower.contains("phone") || lower.contains("mobile")) {
            return SensitiveType.PHONE;
        }
        if (lower.contains("idcard") || lower.contains("idno") || lower.contains("identity")) {
            return SensitiveType.ID_CARD;
        }
        return SensitiveType.MASK;
    }
}
