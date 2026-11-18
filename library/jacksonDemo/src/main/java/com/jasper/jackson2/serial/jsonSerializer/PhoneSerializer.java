package com.jasper.jackson2.serial.jsonSerializer;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 最基础的序列化器抽象类。
 * StdSerializer
 * 提供标准类型信息、Schema、格式访问等辅助能力。
 * StdScalarSerializer
 * 面向标量值的序列化器，例如 String、Integer、Boolean 等。
 */
public class PhoneSerializer
        extends JsonSerializer<String> {

    @Override
    public void serialize(String value, com.fasterxml.jackson.core.JsonGenerator gen, SerializerProvider serializers) throws IOException {

        if (value == null || value.length() != 11) {
            gen.writeString(value);
            return;
        }
        gen.writeString(
                value.substring(0, 3) + "****"
                        + value.substring(7)
        );
    }
}
