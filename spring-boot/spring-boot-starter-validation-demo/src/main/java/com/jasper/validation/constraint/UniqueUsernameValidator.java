package com.jasper.validation.constraint;

import com.jasper.validation.constraint.anno.UniqueUsername;
import com.jasper.validation.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 知识点：校验器注入 Spring Bean。
 *
 * <p>标了 {@link Component} 且用构造器注入，Hibernate Validator 通过
 * SpringConstraintValidatorFactory 从 Spring 容器拿这个 Bean，因此 {@link UserService}
 * 的查库依赖可用。这就是「唯一性校验」不能直接写死在注解 regexp 里的解决方案。
 */
@Component
@RequiredArgsConstructor
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    private final UserService userService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // 空值交给 @NotBlank
        }
        return !userService.existsByUsername(value);
    }
}
