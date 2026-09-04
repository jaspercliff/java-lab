package com.jasper;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Jackson JSON 工具类
 *
 * @version 1.0
 * @author jasper
 * @date 2024-10-21
 */
public class JacksonUtils {

    /**
     * 包含所有字段
     */
    private static class AlwaysMapperHolder {
        private static final ObjectMapper INSTANCE =
                JsonMapper.builder()
                        .changeDefaultPropertyInclusion(incl ->
                                incl.withValueInclusion(ALWAYS)
                        )
                        .build();
    }

    /**
     * 排除 null 值
     */
    private static class NonNullMapperHolder {
        private static final ObjectMapper INSTANCE =
                JsonMapper.builder()
                        .changeDefaultPropertyInclusion(incl ->
                                incl.withValueInclusion(NON_NULL)
                        )
                        .build();
    }

    /**
     * 排除 null 值、空字符串、空集合等
     */
    private static class NonEmptyMapperHolder {
        private static final ObjectMapper INSTANCE =
                JsonMapper.builder()
                        .changeDefaultPropertyInclusion(incl ->
                                incl.withValueInclusion(NON_EMPTY)
                        )
                        .build();
    }

    /**
     * 默认使用 ALWAYS
     */
    public static String toJsonString(Object o) {
        return toJsonString(o, ALWAYS);
    }

    /**
     * JSON → Java 对象
     */
    public static <T> T parseJson(String json, Class<T> valueType) {
        try {
            return AlwaysMapperHolder.INSTANCE.readValue(json, valueType);
        } catch (JacksonException e) {
            throw new RuntimeException(
                    "Error converting JSON string to object: " + e.getMessage(), e
            );
        }
    }

    /**
     * Java 对象 → JSON
     */
    public static String toJsonString(
            Object o,
            JsonInclude.Include include
    ) {
        try {
            return selectMapper(include).writeValueAsString(o);
        } catch (JacksonException e) {
            throw new RuntimeException(
                    "Error converting object to JSON string: " + e.getMessage(), e
            );
        }
    }

    /**
     * 根据序列化策略选择 Mapper
     */
    private static ObjectMapper selectMapper(
            JsonInclude.Include include
    ) {
        return switch (include) {
            case NON_NULL -> NonNullMapperHolder.INSTANCE;
            case NON_EMPTY -> NonEmptyMapperHolder.INSTANCE;
            default -> AlwaysMapperHolder.INSTANCE;
        };
    }
}