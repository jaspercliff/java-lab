package com.jasper.validation.constraint;

import com.jasper.validation.constraint.anno.Phone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 知识点：{@link ConstraintValidator} 实现——泛型第二个参数是「被标注元素类型」。
 *
 * <p>校验器默认是 Spring Bean（由 SpringConstraintValidatorFactory 创建），
 * 因此这里可以 {@code @RequiredArgsConstructor} 注入其他 Bean（本例没有必要，故省略）。
 * 校验器是无状态的：不要存请求级字段，多线程复用。
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    // 1[3-9] 开头 + 9 位数字
    private static final String REGEX = "1[3-9]\\d{9}";

    private boolean nullable;

    @Override
    public void initialize(Phone constraintAnnotation) {
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return nullable; // 空值：nullable=true 放行，false 判错
        }
        return value.matches(REGEX);
    }
}
