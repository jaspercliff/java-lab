package com.jasper.validation.constraint.anno;

import com.jasper.validation.constraint.UniqueUsernameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 知识点：需要「查库」的业务约束——演示在 ConstraintValidator 中注入 Spring Bean。 */
@Documented
@Constraint(validatedBy = UniqueUsernameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUsername {

    String message() default "{com.jasper.validation.constraint.anno.UniqueUsername.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
