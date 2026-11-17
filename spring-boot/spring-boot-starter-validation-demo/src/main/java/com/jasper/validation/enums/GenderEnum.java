package com.jasper.validation.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.jasper.api.IEnum;

/**
 * 知识点：请求 DTO 里的枚举字段，枚举本身实现项目约定的 {@link IEnum}。
 *
 * <p>{@code @JsonValue} 让 Jackson 序列化输出 code（1/2）而不是枚举名，
 * 同时 Jackson 反序列化也按 code 匹配。配合约束注解 {@code @NotNull} 使用。
 */
public enum GenderEnum implements IEnum<Integer> {

    MALE(1, "男"),
    FEMALE(2, "女"),
    UNKNOWN(0, "未知");

    private final Integer code;
    private final String desc;

    GenderEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonValue
    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public static GenderEnum parse(Integer code) {
        return IEnum.parseOf(GenderEnum.class, code);
    }

    /** 供 {@code @EnumValue} 类自定义约束校验 code 是否合法（IEnum.parseOf 找不到会抛异常）。 */
    public static boolean isValidCode(Integer code) {
        if (code == null) {
            return true; // 空值交给 @NotNull 处理，约束注解惯例：不重复报错
        }
        try {
            return parse(code) != null;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
