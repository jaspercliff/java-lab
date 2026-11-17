package com.jasper.springdoc.model;

import com.jasper.springdoc.enums.BookStatus;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;


@Data
@Schema(description = "书籍查询条件（会被展开为多个 query 参数）")
public class BookQuery {

    @Parameter(description = "关键字，模糊匹配书名", example = "spring",
            schema = @Schema(type = "string", maxLength = 50))
    private String keyword;

    @Parameter(description = "按状态过滤", example = "published")
    private BookStatus status;

    @Parameter(description = "标签（可重复传参）",
            array = @ArraySchema(schema = @Schema(type = "string"), maxItems = 5, uniqueItems = true))
    private List<String> tags;

    @Parameter(description = "页码，从 0 开始", example = "0",
            schema = @Schema(type = "integer", minimum = "0", defaultValue = "0"))
    @Min(0)
    private int page = 0;

    @Parameter(description = "每页条数", example = "20",
            schema = @Schema(type = "integer", minimum = "1", maximum = "100", defaultValue = "20"))
    @Min(1) @Max(100)
    private int size = 20;

    /** hidden=true 的参数不会出现在文档里。 */
    @Parameter(hidden = true)
    private String internalOnly;
}
