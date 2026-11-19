package com.jasper.validation.constraint;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;

/**
 * {@link DateRange} 的校验器：反射读取注解指定的两个字段并比较先后。
 */
public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {

    private String startField;
    private String endField;

    @Override
    public void initialize(DateRange annotation) {
        this.startField = annotation.start();
        this.endField = annotation.end();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        LocalDate start = read(value, startField);
        LocalDate end = read(value, endField);
        // 单个字段的空值交由字段自身的 @NotNull 等约束负责
        if (start == null || end == null) {
            return true;
        }
        boolean valid = !start.isAfter(end);
        if (!valid) {
            // 把错误挂到结束字段上，而不是整个对象——返回给前端时字段名更友好
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(endField)
                    .addConstraintViolation();
        }
        return valid;
    }

    private static LocalDate read(Object target, String fieldName) {
        Field field = ReflectionUtils.findField(target.getClass(), fieldName);
        if (field == null) {
            throw new IllegalStateException(
                    String.format(ValidationConstants.FIELD_NOT_READABLE_TEMPLATE, fieldName));
        }
        ReflectionUtils.makeAccessible(field);
        Object fieldValue = ReflectionUtils.getField(field, target);
        if (fieldValue == null) {
            return null;
        }
        if (!(fieldValue instanceof LocalDate date)) {
            throw new IllegalStateException(String.format(
                    ValidationConstants.FIELD_TYPE_MISMATCH_TEMPLATE,
                    fieldName, ValidationConstants.DATE_RANGE_FIELD_TYPE));
        }
        return date;
    }
}
