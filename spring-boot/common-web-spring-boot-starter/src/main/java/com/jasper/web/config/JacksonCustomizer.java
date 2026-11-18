package com.jasper.web.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.jasper.web.config.properties.JacksonProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Jackson 全局约定：统一日期格式与时区、Long 转字符串、忽略 null 字段。
 *
 * <p>通过 postConfigurer 注册 SimpleModule，确保在 Boot 自带的 JavaTimeModule 之后再覆盖序列化器。
 */
@RequiredArgsConstructor
public class JacksonCustomizer implements Jackson2ObjectMapperBuilderCustomizer {

    private final JacksonProperties properties;

    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {
        if (!properties.enabled()) {
            return;
        }
        builder.simpleDateFormat(properties.datePattern())
                .timeZone(TimeZone.getTimeZone(properties.timeZone()))
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        builder.postConfigurer(mapper -> {
            SimpleModule module = new SimpleModule("jasper-jackson");
            if (properties.longToString()) {
                module.addSerializer(Long.class, ToStringSerializer.instance);
                module.addSerializer(Long.TYPE, ToStringSerializer.instance);
                module.addSerializer(BigInteger.class, ToStringSerializer.instance);
            }
            module.addSerializer(LocalDateTime.class,
                    new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(properties.datePattern())));
            module.addSerializer(LocalDate.class,
                    new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            module.addSerializer(LocalTime.class,
                    new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
            mapper.registerModule(module);
        });
    }
}
