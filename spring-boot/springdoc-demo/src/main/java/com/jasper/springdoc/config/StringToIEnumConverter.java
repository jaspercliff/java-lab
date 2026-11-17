package com.jasper.springdoc.config;
import com.jasper.api.IEnum;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class StringToIEnumConverter implements ConditionalGenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Set.of(new ConvertiblePair(String.class, Enum.class));
    }

    @Override
    public boolean matches(
            @NonNull TypeDescriptor sourceType,
            @NonNull TypeDescriptor targetType) {

        Class<?> targetClass = targetType.getObjectType();

        return targetClass.isEnum()
                && IEnum.class.isAssignableFrom(targetClass);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object convert(
            Object source,
            @NonNull TypeDescriptor sourceType,
            @NonNull TypeDescriptor targetType) {

        if (source == null) {
            return null;
        }

        return IEnum.parseOf(
                (Class) targetType.getObjectType(),
                source
        );
    }
}