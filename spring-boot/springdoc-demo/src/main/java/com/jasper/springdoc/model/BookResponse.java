package com.jasper.springdoc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.jasper.springdoc.enums.BookStatus;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * 知识点 D：响应 schema —— Jackson 注解、@Schema 的 readOnly/accessMode、自引用 $ref、@JsonView。
 *
 * <p>{@code @JsonIgnore} 字段完全不进文档；{@code @JsonProperty} 改变文档中的属性名；
 * 自引用类型会生成 {@code $ref: '#/components/schemas/BookResponse'} 而不是无限展开。
 * {@code internalNote} 会被 {@code PropertyCustomizer} 打上 {@code x-sensitive} 扩展。
 */
@Data
@Schema(
        description = "书籍响应",
        requiredProperties = {"id", "title"},
        // 继承/多态：subTypes + discriminator 让 springdoc 生成 oneOf + discriminator
        subTypes = {SpecialBookResponse.class},
        discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = "normal", schema = BookResponse.class),
                @DiscriminatorMapping(value = "special", schema = SpecialBookResponse.class)
        }
)
public class BookResponse {

    @Schema(description = "主键", example = "1001", requiredMode = Schema.RequiredMode.REQUIRED,
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "书名", example = "Springdoc in Action",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "状态", example = "published")
    private BookStatus status;

    @Schema(description = "定价", example = "88.50", format = "decimal")
    private BigDecimal price;

    @Schema(description = "创建时间", type = "string", format = "date-time",
            example = "2026-11-16T10:00:00+08:00", accessMode = Schema.AccessMode.READ_ONLY)
    private OffsetDateTime createdAt;

    @Schema(description = "封面地址；@JsonProperty 会让文档里显示为 cover_url",
            example = "https://cdn.example.com/covers/1001.png")
    @JsonProperty("cover_url")
    private String coverUrl;

    @Schema(description = "标签", example = "[\"java\",\"openapi\"]")
    private List<String> tags;

    /** 自引用：springdoc 会输出 $ref，避免递归展开。 */
    @Schema(description = "关联书籍（自引用，文档中表现为 $ref）")
    private BookResponse related;

    @Schema(description = "内部备注，仅 Admin 视图可见", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonView(Views.Admin.class)
    private String internalNote;

    @Schema(description = "被 hidden=true 隐藏的字段", hidden = true)
    private String debugFlag;

    /** 被 Jackson 忽略，文档里彻底不存在。 */
    @JsonIgnore
    private String serverSecret;
}
