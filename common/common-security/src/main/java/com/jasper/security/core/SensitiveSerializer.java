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
 * 读取字段上的 {@link Sensitive} 注解并按其策略序列化。 <br/>
 * JsonSerializer<Object>：负责定义怎么把 Java 对象转换成 JSON。<br/>
 * ContextualSerializer：负责根据当前字段的上下文动态创建或调整序列化器 当前字段有什么注解、类型、配置 <br>
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

    /**
     *  这里一定要返回新的对象，如果俩个字段共享一个实例，之前的配置可能被覆盖
     * @param property 它代表当前正在处理的 Java Bean 属性
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        if (property == null) {
            return this;
        }
        // 属性上
        Sensitive annotation = property.getAnnotation(Sensitive.class);
        if (annotation == null) {
            // 类上
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
