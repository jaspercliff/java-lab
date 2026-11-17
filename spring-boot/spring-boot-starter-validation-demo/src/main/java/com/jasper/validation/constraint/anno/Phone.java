package com.jasper.validation.constraint.anno;

import com.jasper.validation.constraint.FatalPayload;
import com.jasper.validation.constraint.PhoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 知识点：字段级自定义约束注解（中国大陆手机号）。
 *
 * <p>一个约束注解三要素：
 * <ol>
 *   <li>{@code @Constraint(validatedBy = ...)} 指定校验器；</li>
 *   <li>必须包含 {@code message()} / {@code groups()} / {@code payload()} 三个属性；</li>
 *   <li>{@code @Target} 决定能标在哪（FIELD/PARAMETER/ANNOTATION_TYPE...）。</li>
 * </ol>
 *
 * <p>{@code payload} 可在使用处带 {@link FatalPayload} 演示：把「这个错误很严重」的语义
 * 随约束带出去，全局异常处理器可据此改变响应（HTTP 状态/错误码）。
 */
@Documented
@Constraint(validatedBy = PhoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Phone {

    String message() default "{com.jasper.validation.constraint.anno.Phone.message}";

    Class<?>[] groups() default {};

    // 规范强制要求约束注解的 payload 默认值为空数组；若要带 payload 必须在「使用处」显式指定，
    // 例如 @Phone(payload = FatalPayload.class)。见 UserCreateRequest.phone。
    Class<? extends Payload>[] payload() default {};

    /** 是否允许为空——把「非空」和「格式」拆开，空值交给 {@code @NotBlank}，符合单一职责。 */
    boolean nullable() default true;
}
