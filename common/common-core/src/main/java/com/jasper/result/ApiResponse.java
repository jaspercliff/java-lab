package com.jasper.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.MDC;
import java.time.Instant;

/**
 * 通用 API 响应对象 (JDK 21 Record 实现)
 * 
 * @param <T> 返回数据的泛型
 * @param code    业务状态码 (200成功, 其他失败)
 * @param message 提示信息
 * @param data    实际数据载荷
 * @param timestamp 响应时间戳 (毫秒)
 * @param traceId 链路追踪ID (自动从 MDC 获取)
 */
// 使用 JsonInclude 忽略 null 字段，让返回的 JSON 更干净
@JsonInclude(JsonInclude.Include.NON_NULL) 
public record ApiResponse<T>(
        int code,
        String message,
        T data,
        long timestamp,
        String traceId
) {

    // ================= 静态工厂方法=================

    /**
     * 成功响应 (带数据)
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(),
                                 data, currentTimestamp(), currentTraceId());
    }

    /**
     * 成功响应 (无数据)
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    /**
     * 失败响应 (使用自定义状态码和消息)
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null, currentTimestamp(), currentTraceId());
    }

    /**
     * 失败响应 (使用 ResultCode 枚举)
     */
    public static <T> ApiResponse<T> error(ResultCode resultCode) {
        return error(resultCode.getCode(), resultCode.getMsg());
    }

    /**
     * 默认系统异常响应 (500)
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message);
    }

    // ================= 私有辅助方法 =================

    private static long currentTimestamp() {
        return Instant.now().toEpochMilli();
    }

    /**
     * 自动从 SLF4J MDC 中获取 traceId
     * 前提：你的拦截器/过滤器中已经将 traceId 放入了 MDC (如: MDC.put("traceId", id))
     */
    private static String currentTraceId() {
        String traceId = MDC.get("traceId");
        // 兼容处理：如果 MDC 中没有，则返回 null (JSON 序列化时会被忽略)
        return (traceId == null || traceId.isBlank()) ? null : traceId;
    }
}