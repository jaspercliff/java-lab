package com.jasper.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类级约束（跨字段校验）：起始日期不能晚于结束日期。
 *
 * <p>标在 {@link ElementType#TYPE} 上，校验器拿到整个对象，适合「字段之间互相比较」
 * 这类单个字段无法表达的规则。
 */
@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateRange {

    String MESSAGE_KEY = ValidationConstants.DATE_RANGE_MESSAGE_KEY;

    String message() default "{" + MESSAGE_KEY + "}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /** 起始字段名，对应字段类型需为 {@code java.time.LocalDate}。 */
    String start();

    /** 结束字段名，对应字段类型需为 {@code java.time.LocalDate}。 */
    String end();
}
