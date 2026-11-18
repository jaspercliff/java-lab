package com.jasper.web.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/** 请求访问日志配置，前缀 {@code jasper.web.access-log}。敏感字段配置见 {@link com.jasper.security.config.SensitiveProperties}。 */
@ConfigurationProperties(prefix = "jasper.web.access-log")
public record AccessLogProperties(

        // 是否开启访问日志，默认开启。
        @DefaultValue("true")
        boolean enabled,

        // 超过该耗时（毫秒）按 WARN 记录，默认 1000。
        @DefaultValue("1000")
        long slowThresholdMs,

        // 从 MDC 读取 traceId 的 key，默认 traceId。
        @DefaultValue("traceId")
        String mdcKey,

        // 是否记录请求体（含 path variable 之外的表单/JSON body），默认开启。
        @DefaultValue("true")
        boolean includeRequestBody,

        // 是否记录响应体，默认开启。
        @DefaultValue("true")
        boolean includeResponseBody,

        // 请求体/响应体最大记录长度（字符），超出截断，默认 2048。
        @DefaultValue("2048")
        int maxBodyLength
) {
}
