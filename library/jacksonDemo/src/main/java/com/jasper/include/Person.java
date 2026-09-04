package com.jasper.include;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class Person {
    @JsonInclude(JsonInclude.Include.ALWAYS) //总是包含 默认
    private Integer id;
    @JsonInclude(JsonInclude.Include.NON_NULL) //不为空时
    private String name;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)// 不为null和空时
    private String address;
    /**
     * int/long/short/byte → 0
     * float/double → 0.0
     * boolean → false
     * 引用类型（String, Object 等）→ null
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT) //不为默认值时
    private long age;
    /**
     * ObjectMapper mapper = new ObjectMapper();
     * mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
     */
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS) //使用类或属性的默认行为 通常是always 但也可能全局配置为别的行为
    private String phone;
    @JsonInclude(value = JsonInclude.Include.CUSTOM,valueFilter = SkipSpecialFilter.class)
    private String description;
    @JsonInclude(value = JsonInclude.Include.NON_ABSENT)//效果和non-null 类似 但是排除 optional.empty() atomicReference null
    private Role role;
}
