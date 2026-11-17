package com.jasper.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 知识点：类级约束（跨字段校验）——开始时间必须早于结束时间。
 *
 * <p>标在 TYPE 上，校验器拿到的是整个对象，适合「字段之间互相比较」这类单个字段无法表达的规则。
 * 等价于 Hibernate Validator 的 {@code @ScriptAssert}，但类型安全、可复用。
 */
@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateRange {

    String message() default "{com.jasper.validation.constraint.DateRange.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /** 起始字段名（该字段需为 java.time.LocalDate）。 */
    String start();

    /** 结束字段名。 */
    String end();
}
