package com.jasper.springdoc.config;

import com.jasper.result.ApiResult;
import com.jasper.result.ResultCode;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局响应文档 + 统一异常响应。
 *
 * <p>类上的 {@link ApiResponses} 会被 springdoc 扫描；配合
 * {@code springdoc.override-with-generic-response=true}，这些响应会自动合并进「每一个」operation，
 * 不用在每个方法上重复写 429/500。
 *
 * <p>按工程约定，异常响应也使用 common 工程的 {@link ApiResult}，不再返回
 * {@code ProblemDetail}；HTTP 状态码与业务码分别由 {@code ResponseEntity} 和 {@code ResultCode} 表达。
 */
@RestControllerAdvice
// @ApiResponses({
//         @ApiResponse(
//                 responseCode = "429",
//                 description = "触发限流",
//                 headers = @Header(name = "Retry-After", description = "多少秒后可重试",
//                         schema = @Schema(type = "integer", example = "30")),
//                 content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
//                         schema = @Schema(implementation = ApiResult.class))
//         ),
//         @ApiResponse(
//                 responseCode = "500",
//                 description = "服务内部错误",
//                 content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
//                         schema = @Schema(implementation = ApiResult.class))
//         )
// })
public class DocumentedExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResult<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResult.error(ResultCode.BAD_REQUEST.getCode(), ex.getMessage()));
    }
}
