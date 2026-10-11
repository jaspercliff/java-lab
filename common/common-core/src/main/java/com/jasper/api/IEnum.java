package com.jasper.api;

import java.util.Arrays;

public interface IEnum<T> {
    T getCode();
    String getDesc();

    /**
     * 通用的枚举查找方法（支持字符串去空格、忽略大小写、以及找不到时抛出异常）
     */
    static <E extends Enum<E> & IEnum<T>, T> E parseOf(Class<E> clazz, T code) {
        if (code == null) {
            return null;
        }

        // 如果是 String 类型，自动去空格
        Object targetCode = code;
        if (code instanceof String) {
            targetCode = ((String) code).trim();
        }

        for (E constant : clazz.getEnumConstants()) {
            T constantCode = constant.getCode();

            // 如果双方都是 String，支持忽略大小写匹配；其他类型（如 Integer）走普通 equals
            if (constantCode instanceof String && targetCode instanceof String) {
                if (((String) constantCode).equalsIgnoreCase((String) targetCode)) {
                    return constant;
                }
            } else {
                if (constantCode.equals(targetCode)) {
                    return constant;
                }
            }
        }

        // 找不到时抛出异常，并自动拼接当前枚举所有支持的值
        String supportedValues = Arrays.stream(clazz.getEnumConstants())
                .map(e -> String.valueOf(e.getCode()))
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        throw new IllegalArgumentException("Unknown value: [" + code + "] for enum " + clazz.getSimpleName()
                + ". Supported values are: " + supportedValues);
    }
}
