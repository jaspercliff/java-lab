
package com.jasper.api;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

public interface IEnum<T> {

    T getCode();

    String getDesc();

    /**
     * 通用的枚举查找方法
     * 支持：
     * 1. String 去空格、忽略大小写
     * 2. String 自动转换为 Integer、Long 等数字类型
     * 3. 找不到匹配项时抛出异常
     */
    static <E extends Enum<E> & IEnum<T>, T> E parseOf(
            Class<E> clazz, Object code) {

        if (code == null) {
            return null;
        }

        E[] constants = clazz.getEnumConstants();

        if (constants.length == 0) {
            throw new IllegalArgumentException(
                    "Enum " + clazz.getSimpleName() + " has no constants");
        }

        // 根据枚举实际 code 类型转换输入值
        Object targetCode = convertCode(code, constants[0].getCode().getClass());

        for (E constant : constants) {
            T constantCode = constant.getCode();

            if (constantCode instanceof String && targetCode instanceof String) {
                if (((String) constantCode).equalsIgnoreCase((String) targetCode)) {
                    return constant;
                }
            } else if (constantCode.equals(targetCode)) {
                return constant;
            }
        }

        String supportedValues = Arrays.stream(constants)
                .map(e -> String.valueOf(e.getCode()))
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        throw new IllegalArgumentException(
                "Unknown value: [" + code + "] for enum "
                        + clazz.getSimpleName()
                        + ". Supported values are: " + supportedValues);
    }

    /**
     * 将输入值转换为枚举 code 对应的类型
     */
    private static Object convertCode(Object code, Class<?> targetType) {

        if (targetType.isInstance(code)) {
            return code instanceof String
                    ? ((String) code).trim()
                    : code;
        }

        String value = code.toString().trim();

        if (targetType == String.class) {
            return value;
        }

        if (targetType == Integer.class) {
            return Integer.valueOf(value);
        }

        if (targetType == Long.class) {
            return Long.valueOf(value);
        }

        if (targetType == Short.class) {
            return Short.valueOf(value);
        }

        if (targetType == Byte.class) {
            return Byte.valueOf(value);
        }

        if (targetType == Double.class) {
            return Double.valueOf(value);
        }

        if (targetType == Float.class) {
            return Float.valueOf(value);
        }

        if (targetType == BigInteger.class) {
            return new BigInteger(value);
        }

        if (targetType == BigDecimal.class) {
            return new BigDecimal(value);
        }

        throw new IllegalArgumentException(
                "Unsupported enum code type: " + targetType.getName());
    }
}
