package com.jasper.validation.constraint;

/**
 * 校验注解相关的字符串常量集中定义，避免散落的魔法值。
 */
public final class ValidationConstants {

    /** DateRange 约束消息 key，需与 ValidationMessages.properties / 业务方 i18n 配置一致。 */
    public static final String DATE_RANGE_MESSAGE_KEY =
            "com.jasper.validation.constraint.DateRange.message";

    /** 字段不存在或无法读取时的异常消息模板。 */
    public static final String FIELD_NOT_READABLE_TEMPLATE = "字段 %s 不存在或无法读取";

    /** 字段类型不符合约束时的异常消息模板。 */
    public static final String FIELD_TYPE_MISMATCH_TEMPLATE = "字段 %s 类型必须为 %s";

    /** DateRange 约束支持的字段类型名称。 */
    public static final String DATE_RANGE_FIELD_TYPE = "java.time.LocalDate";

    private ValidationConstants() {
    }
}
