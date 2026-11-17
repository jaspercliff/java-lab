package com.jasper.springdoc.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/** 知识点：错误响应 schema（既可用自定义结构，也可用 RFC 7807 的 Spring ProblemDetail）。 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "统一错误响应")
public class ApiError {

    @Schema(description = "业务错误码", example = "BOOK_NOT_FOUND", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "错误描述", example = "书籍不存在", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    @Schema(description = "链路追踪 ID", example = "4bf92f3577b34da6a3ce929d0e0e4736")
    private String traceId;

    @Schema(description = "发生时间", type = "string", format = "date-time")
    private Instant timestamp;

    @Schema(description = "字段级校验错误详情")
    private Map<String, String> details;
}
