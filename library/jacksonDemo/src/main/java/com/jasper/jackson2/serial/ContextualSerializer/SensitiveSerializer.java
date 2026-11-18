package com.jasper.jackson2.serial.ContextualSerializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class SensitiveSerializer
        extends StdSerializer<String>
        implements ContextualSerializer {

    private final SensitiveType type;

    public SensitiveSerializer() {
        this(null);
    }

    public SensitiveSerializer(SensitiveType type) {
        super(String.class);
        this.type = type;
    }

    /**
     * SerializerProvider	Jackson 的序列化上下文，可以获取配置、其他序列化器等
     * BeanProperty	当前正在处理的属性，可以获取字段名、类型、注解等
     */
    @Override
    public JsonSerializer<?> createContextual(
            SerializerProvider provider,
            BeanProperty property
    ) throws JsonMappingException {

        if (property == null) {
            return this;
        }

        Sensitive sensitive =
                property.getAnnotation(Sensitive.class);

        if (sensitive == null) {
            return this;
        }
        //这里一定要new 避免配置覆盖
        return new SensitiveSerializer(sensitive.value());
    }

    @Override
    public void serialize(
            String value,
            JsonGenerator gen,
            SerializerProvider provider
    ) throws IOException {

        if (value == null) {
            gen.writeNull();
            return;
        }

        String result = switch (type) {
            case PHONE -> maskPhone(value);
            case EMAIL -> maskEmail(value);
            case ID_CARD -> maskIdCard(value);
        };

        gen.writeString(result);
    }

    private String maskPhone(String value) {
        if (value.length() != 11) {
            return value;
        }

        return value.substring(0, 3)
                + "****"
                + value.substring(7);
    }

    private String maskEmail(String value) {
        int index = value.indexOf("@");

        if (index <= 0) {
            return value;
        }

        return value.charAt(0)
                + "***"
                + value.substring(index);
    }

    private String maskIdCard(String value) {
        if (value.length() < 8) {
            return value;
        }

        return value.substring(0, 3)
                + "***********"
                + value.substring(value.length() - 4);
    }
}