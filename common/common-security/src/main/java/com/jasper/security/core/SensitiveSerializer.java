package com.jasper.security.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.jasper.security.annotation.Sensitive;

import java.io.IOException;

/**
 * 读取字段上的 {@link Sensitive} 注解并按其策略序列化。
 *
 * <p>实现 {@link ContextualSerializer} 是为了把注解上的枚举值传进 serializer 实例，
 * 这样同一个注解可以表达多种脱敏策略。
 */
public class SensitiveSerializer extends JsonSerializer<Object> implements ContextualSerializer {

    private final SensitiveType type;

    public SensitiveSerializer() {
        this(SensitiveType.MASK);
    }

    public SensitiveSerializer(SensitiveType type) {
        this.type = type;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        if (property == null) {
            return this;
        }
        Sensitive annotation = property.getAnnotation(Sensitive.class);
        if (annotation == null) {
            annotation = property.getContextAnnotation(Sensitive.class);
        }
        return annotation == null ? this : new SensitiveSerializer(annotation.value());
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(SensitiveType.mask(value == null ? null : value.toString(), type));
    }

    @Override
    public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider serializers,
                                  TypeSerializer typeSer) throws IOException {
        serialize(value, gen, serializers);
    }
}
