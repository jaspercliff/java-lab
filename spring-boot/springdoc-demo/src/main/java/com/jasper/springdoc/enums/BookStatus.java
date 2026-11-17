package com.jasper.springdoc.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.jasper.api.IEnum;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 知识点：枚举 schema。
 *
 * <p>项目约定所有枚举实现 {@link IEnum}；{@link JsonValue} 让「文档中的 enum 取值」与
 * 「JSON 实际传输的 code」保持一致（否则 springdoc 只会列出 DRAFT/PUBLISHED 这些常量名）。
 */
@Schema(description = "书籍状态（JSON 中使用小写 code，而非枚举常量名）", example = "published")
public enum BookStatus implements IEnum<String> {

    DRAFT("draft", "草稿"),
    PUBLISHED("published", "已发布"),
    ARCHIVED("archived", "已归档");

    private final String code;
    private final String desc;

    BookStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonValue
    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public static BookStatus parse(String code) {
        return IEnum.parseOf(BookStatus.class, code);
    }
}
