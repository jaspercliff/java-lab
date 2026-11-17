package com.jasper.springdoc.model;

import com.jasper.springdoc.enums.BookCategory;
import com.jasper.springdoc.enums.BookStatus;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * 知识点 C：请求体 schema —— Java record + Bean Validation + {@code @Schema} 字段级全属性。
 *
 * <p>springdoc 会把 Jakarta Validation 约束自动翻译成 OpenAPI 关键字：
 * {@code @NotBlank → required + minLength}，{@code @Size → minLength/maxLength}，
 * {@code @Pattern → pattern}，{@code @DecimalMin/Max → minimum/maximum}，
 * {@code @Min/@Max → minimum/maximum}，{@code @Email → format: email}。
 * {@code @Schema} 用来补 Validation 表达不了的信息（示例、默认值、扩展、隐藏字段等）。
 */
@Schema(
        name = "BookCreateRequest",
        title = "创建书籍请求",
        description = "演示 Bean Validation 与 @Schema 叠加时生成的完整 schema",
        requiredProperties = {"title", "isbn", "price"},
        extensions = @Extension(name = "x-model", properties = {
                @ExtensionProperty(name = "style", value = "java record"),
                @ExtensionProperty(name = "validated", value = "true", parseValue = true)
        })
)
public record BookCreateRequest(

        @Schema(description = "书名", example = "Springdoc in Action",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 1, maxLength = 120, pattern = "^[A-Za-z0-9 ]+$")
        @NotBlank @Size(min = 1, max = 120) @Pattern(regexp = "^[A-Za-z0-9 ]+$")
        String title,

        @Schema(description = "ISBN-13", example = "978-1-0000-0000-1",
                requiredMode = Schema.RequiredMode.REQUIRED, pattern = "^97[89]-[0-9-]{10,17}$")
        @NotBlank @Pattern(regexp = "^97[89]-[0-9-]{10,17}$")
        String isbn,

        @Schema(description = "定价（元）", example = "88.50", format = "decimal",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minimum = "0.01", maximum = "9999.99", multipleOf = 0.01,
                exclusiveMinimum = true)
        @NotNull @DecimalMin("0.01") @DecimalMax("9999.99") @Digits(integer = 4, fraction = 2)
        BigDecimal price,

        @Schema(description = "状态，取 IEnum 的 code", example = "published",
                defaultValue = "draft",
                allowableValues = {"draft", "published", "archived"},
                implementation = BookStatus.class)
        @NotNull
        BookStatus status,

        @Schema(description = "分类（整数码枚举）", example = "1", implementation = BookCategory.class)
        BookCategory category,

        @Schema(description = "标签列表")
        @ArraySchema(
                schema = @Schema(description = "单个标签", example = "java", minLength = 1, maxLength = 20),
                minItems = 0, maxItems = 5, uniqueItems = true)
        @Size(max = 5)
        List<String> tags,

        @Schema(description = "出版时间（ISO-8601）", type = "string", format = "date-time",
                example = "2026-11-16T10:00:00+08:00")
        OffsetDateTime publishedAt,

        @Schema(description = "联系人邮箱", format = "email", example = "dev@example.com")
        @Email
        String contactEmail,

        @Schema(description = "库存", minimum = "0", maximum = "100000", defaultValue = "0",
                example = "42")
        @Min(0) @Max(100000)
        Integer stock,

        @Schema(description = "可空备注", nullable = true, example = "null")
        String nullableNote,

        @Schema(description = "只写字段：仅出现在请求 schema", accessMode = Schema.AccessMode.WRITE_ONLY)
        String writeOnlyHint,

        @Schema(description = "已废弃字段", deprecated = true)
        @Deprecated
        String legacyField,

        @Schema(description = "结构化扩展字段", example = "{\"source\":\"lab\"}")
        Map<String, Object> metadata,

        // 隐藏字段：JSON 里仍可传，但不会出现在 OpenAPI schema 中
        @Schema(hidden = true)
        String internalSecret
) {
}
