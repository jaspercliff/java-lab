package com.jasper.springdoc.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.jasper.api.IEnum;
import io.swagger.v3.oas.annotations.media.Schema;

/** 知识点：整数码枚举同样走 {@link IEnum} + {@link JsonValue}，schema 会输出 numeric enum。 */
@Schema(description = "书籍分类（整数码）", example = "1")
public enum BookCategory implements IEnum<Integer> {

    TECH(1, "技术"),
    LITERATURE(2, "文学"),
    OTHER(99, "其他");

    private final Integer code;
    private final String desc;

    BookCategory(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonValue
    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public static BookCategory parse(Integer code) {
        return IEnum.parseOf(BookCategory.class, code);
    }
}
