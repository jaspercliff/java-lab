
package com.jasper.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.MDC;

import java.time.Instant;

/**
 * 统一 API 响应结构。
 *
 * <p>用于规范接口成功和失败时的响应格式，包含：
 * <ul>
 *     <li>code：响应码，系统码为 "400"/"500" 字符串，业务码为 "RB1001" 形式</li>
 *     <li>message：国际化后的响应描述</li>
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
public record ApiResult<T>(
        String code,
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
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(
                String.valueOf(ResultCode.SUCCESS.getCode()),
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
    public static <T> ApiResult<T> success() {
        return success(null);
    }

    /** 兼容旧的 int 系统码调用方式。 */
    public static <T> ApiResult<T> error(int code, String message) {
        return error(String.valueOf(code), message);
    }

    /** 兼容旧的 int 系统码调用方式。 */
    public static <T> ApiResult<T> error(int code, String message, T data) {
        return error(String.valueOf(code), message, data);
    }

    /**
     * 构建失败响应，code 可传系统码字符串或业务码（如 RB1001）。
     *
     * @param code    响应码
     * @param message 已国际化的错误描述
     * @param <T>     响应数据类型
     * @return 失败响应
     */
    public static <T> ApiResult<T> error(String code, String message) {
        return new ApiResult<>(
                code,
                message,
                null,
                Instant.now().toEpochMilli(),
                currentTraceId()
        );
    }

    public static <T> ApiResult<T> error(String code, String message, T data) {
        return new ApiResult<>(
                code,
                message,
                data,
                Instant.now().toEpochMilli(),
                currentTraceId()
        );
    }

    /**
     * 根据系统错误码构建失败响应。
     *
     * @param resultCode 系统错误码
     * @param <T>        响应数据类型
     * @return 失败响应
     */
    public static <T> ApiResult<T> error(ResultCode resultCode) {
        return error(String.valueOf(resultCode.getCode()), resultCode.getMsg());
    }

    /**
     * 根据业务错误码构建失败响应，code 为 RB1001 形式。
     *
     * @param businessCode 业务错误码
     * @param message      已国际化的错误描述
     * @param <T>          响应数据类型
     * @return 失败响应
     */
    public static <T> ApiResult<T> error(IBusinessResultCode businessCode, String message) {
        return error(businessCode.getCode(), message);
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
