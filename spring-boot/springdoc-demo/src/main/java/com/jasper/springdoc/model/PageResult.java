package com.jasper.springdoc.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 知识点：泛型响应包装。springdoc 会把 {@code PageResult<BookResponse>} 展开成
 * {@code allOf: [PageResult, { properties.content.items: $ref BookResponse }]}，
 * 而不是把泛型擦除成 Object。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通用分页包装")
public class PageResult<T> {

    @Schema(description = "当前页数据")
    private List<T> content;

    @Schema(description = "总记录数", example = "128", minimum = "0")
    private long total;

    @Schema(description = "页码，从 0 开始", defaultValue = "0", minimum = "0", example = "0")
    private int page;

    @Schema(description = "每页条数", defaultValue = "20", minimum = "1", maximum = "100", example = "20")
    private int size;
}
