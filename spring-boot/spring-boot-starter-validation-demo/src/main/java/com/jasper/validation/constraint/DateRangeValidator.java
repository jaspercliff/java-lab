package com.jasper.validation.constraint;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.time.LocalDate;

/** 类级约束校验器：反射读取注解里指定的两个字段名并比较。 */
public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {

    private String startField;
    private String endField;

    @Override
    public void initialize(DateRange annotation) {
        this.startField = annotation.start();
        this.endField = annotation.end();
    }

    /**
     * @param value object to validate    dto object UserCreateRequest
     * @param context context in which the constraint is evaluated
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        LocalDate start = read(value, startField);
        LocalDate end = read(value, endField);
        if (start == null || end == null) {
            return true; // 单个字段的空值由各自的 @NotNull 负责
        }
        boolean valid = !start.isAfter(end);
        if (!valid) {
            // 把错误挂到具体属性上，而不是整个对象——返回给前端时字段名更友好
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(endField)
                    .addConstraintViolation();
        }
        return valid;
    }

    private static LocalDate read(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object v = field.get(target);
            return v instanceof LocalDate d ? d : null;
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("字段 " + fieldName + " 不存在或无法读取", ex);
        }
    }
}
