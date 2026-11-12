
package com.jasper.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.MDC;

import java.time.Instant;

/**
 * 统一 API 响应结构。
 *
 * <p>用于规范接口成功和失败时的响应格式，包含：
 * <ul>
 *     <li>code：业务响应码</li>
 *     <li>message：响应描述</li>
 *     <li>data：响应数据</li>
 *     <li>timestamp：响应时间戳（毫秒）</li>
 *     <li>traceId：请求追踪 ID</li>
 * </ul>
 *
 * <p>通过 {@link JsonInclude} 忽略值为 null 的字段，
 * 避免响应中出现不必要的空字段。
 *
 * @param <T> 响应数据类型
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        int code,
        String message,
        T data,
        long timestamp,
        String traceId
) {

    /**
     * 构建成功响应。
     *
     * @param data 响应数据
     * @param <T>  响应数据类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                ResultCode.SUCCESS.getCode(),
                ResultCode.SUCCESS.getMsg(),
                data,
                Instant.now().toEpochMilli(),
                currentTraceId()
        );
    }

    /**
     * 构建无响应数据的成功响应。
     *
     * @param <T> 响应数据类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    /**
     * 构建失败响应。
     *
     * @param code    业务错误码
     * @param message 错误描述
     * @param <T>     响应数据类型
     * @return 失败响应
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(
                code,
                message,
                null,
                Instant.now().toEpochMilli(),
                currentTraceId()
        );
    }

    /**
     * 根据统一错误码构建失败响应。
     *
     * @param resultCode 统一错误码
     * @param <T>        响应数据类型
     * @return 失败响应
     */
    public static <T> ApiResponse<T> error(ResultCode resultCode) {
        return error(resultCode.getCode(), resultCode.getMsg());
    }

    /**
     * 获取当前请求的 TraceId。
     *
     * <p>从 MDC 中读取 traceId。
     * 如果当前线程没有绑定 TraceId，则返回 null。
     *
     * @return 当前请求的 TraceId
     */
    private static String currentTraceId() {
        String traceId = MDC.get("traceId");
        return traceId != null && !traceId.isBlank() ? traceId : null;
    }
}
