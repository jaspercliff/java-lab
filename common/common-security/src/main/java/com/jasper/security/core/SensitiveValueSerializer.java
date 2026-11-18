package com.jasper.security.core;

import com.jasper.security.annotation.Sensitive;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.jsontype.TypeSerializer;

/**
 * Jackson 3 版的脱敏序列化器（Jackson 3 把 {@code JsonSerializer} 改名为 {@code ValueSerializer}）。
 *
 * <p>读取字段/类上的 {@link Sensitive} 注解并按其策略序列化。
 * 实现上下文序列化是为了把注解上的策略枚举传进实例，同一个注解可表达多种脱敏策略。
 * 每个字段必须 new 新实例，避免多个字段共享实例导致配置互相覆盖。
 */
public class SensitiveValueSerializer extends ValueSerializer<Object> {

    private final SensitiveType type;

    public SensitiveValueSerializer() {
        this(SensitiveType.MASK);
    }

    public SensitiveValueSerializer(SensitiveType type) {
        this.type = type;
    }

    @Override
    public ValueSerializer<?> createContextual(SerializationContext context, BeanProperty property) {
        if (property == null) {
            return this;
        }
        Sensitive annotation = property.getAnnotation(Sensitive.class);
        if (annotation == null) {
            annotation = property.getContextAnnotation(Sensitive.class);
        }
        return annotation == null ? this : new SensitiveValueSerializer(annotation.value());
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializationContext context) {
        gen.writeString(SensitiveType.mask(value == null ? null : value.toString(), type));
    }

    @Override
    public void serializeWithType(Object value, JsonGenerator gen, SerializationContext context,
                                  TypeSerializer typeSer) {
        serialize(value, gen, context);
    }
}
