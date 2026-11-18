package com.jasper.security.core;

import com.jasper.api.IEnum;

/** 脱敏策略，供 {@code @Sensitive} 注解和字段名匹配器共用。 */
public enum SensitiveType implements IEnum<String> {

    /** 全部遮蔽，适用于密码、token 等。 */
    MASK("mask", "全遮蔽") {
        @Override
        public String apply(String value) {
            return "******";
        }
    },

    /** 保留首尾各一位，中间遮蔽。 */
    KEEP_EDGE("keep_edge", "保留首尾") {
        @Override
        public String apply(String value) {
            if (value.length() <= 2) {
                return "******";
            }
            return value.charAt(0) + "******" + value.charAt(value.length() - 1);
        }
    },

    /** 手机号：保留前三位和后四位。非 11 位退化为 KEEP_EDGE。 */
    PHONE("phone", "手机号保留前3后4") {
        @Override
        public String apply(String value) {
            if (value.length() != 11) {
                return KEEP_EDGE.apply(value);
            }
            return value.substring(0, 3) + "****" + value.substring(7);
        }
    },

    /** 身份证号：18 位保留前 4 后 4，15 位保留前 3 后 4，其余退化为 KEEP_EDGE。 */
    ID_CARD("id_card", "身份证号保留前4后4") {
        @Override
        public String apply(String value) {
            if (value.length() == 18) {
                return value.substring(0, 4) + "**********" + value.substring(14);
            }
            if (value.length() == 15) {
                return value.substring(0, 3) + "********" + value.substring(11);
            }
            return KEEP_EDGE.apply(value);
        }
    };

    private final String code;
    private final String desc;

    SensitiveType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public abstract String apply(String value);

    /** null / 空串原样返回，避免脱敏把缺失值变成星号。 */
    public static String mask(String value, SensitiveType type) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return (type == null ? MASK : type).apply(value);
    }
}
