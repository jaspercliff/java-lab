package com.jasper.jackson2.serial.StdSerializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class PhoneSerializer extends StdSerializer<String> {

    public PhoneSerializer() {
        super(String.class);
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

        gen.writeString(
                value.substring(0, 3) + "****"
                        + value.substring(7)
        );
    }
}