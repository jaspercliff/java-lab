package com.jasper.web.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jasper.monitor.TimeTracker;
import com.jasper.security.core.SensitiveFieldMatcher;
import com.jasper.security.core.SensitiveTextMasker;
import com.jasper.web.config.properties.AccessLogProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 访问日志过滤器：在一条日志中记录 method、target（含脱敏后的 path variable 与 query）、
 * 请求体、状态码、响应体、耗时与 traceId；慢请求或 5xx 提升为 WARN。
 *
 * <p>请求/响应体通过 {@link ContentCachingRequestWrapper} / {@link ContentCachingResponseWrapper}
 * 缓存，避免消费掉下游流；响应体在 finally 中 {@code copyBodyToResponse()} 写回，
 * 保证业务响应不因日志而丢失。
 *
 * <p>path variable 的判断：Filter 在 DispatcherServlet 之前执行，进入时无法得知变量名，
 * 因此在 {@code doFilter} 之后读取 {@link HandlerMapping#BEST_MATCHING_PATTERN_ATTRIBUTE}
 * （如 {@code /user/{phone}}）与 {@link HandlerMapping#URI_TEMPLATE_VARIABLES_ATTRIBUTE}
 * （变量名→值），按变量名用 {@link SensitiveFieldMatcher} 脱敏后渲染回模板。
 *
 * <p>脱敏规则与日志 Converter、校验异常共用同一份 {@link SensitiveFieldMatcher} 配置。
 */
@Slf4j
@RequiredArgsConstructor
public class AccessLogFilter extends OncePerRequestFilter {

    private final AccessLogProperties properties;
    private final SensitiveFieldMatcher sensitiveMatcher;
    private final ObjectMapper objectMapper;

    public AccessLogFilter(AccessLogProperties properties, SensitiveFieldMatcher sensitiveMatcher) {
        this(properties, sensitiveMatcher, new ObjectMapper());
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        TimeTracker tracker = TimeTracker.silent("access");

        ContentCachingRequestWrapper cachedRequest = properties.includeRequestBody()
                ? new ContentCachingRequestWrapper(request, properties.maxBodyLength())
                : null;
        ContentCachingResponseWrapper cachedResponse = properties.includeResponseBody()
                ? new ContentCachingResponseWrapper(response)
                : null;

        HttpServletRequest upstreamRequest = cachedRequest == null ? request : cachedRequest;
        HttpServletResponse upstreamResponse = cachedResponse == null ? response : cachedResponse;

        try {
            filterChain.doFilter(upstreamRequest, upstreamResponse);
        } finally {
            try {
                String requestBody = cachedRequest == null ? "" : readRequestBody(cachedRequest);
                String responseBody = cachedResponse == null ? "" : readResponseBody(cachedResponse);
                logAccess(request, response, requestBody, responseBody, tracker.elapsedMillis());
            } finally {
                // 必须写回，否则业务响应体丢失
                if (cachedResponse != null) {
                    cachedResponse.copyBodyToResponse();
                }
                tracker.close();
            }
        }
    }

    private void logAccess(HttpServletRequest request, HttpServletResponse response,
                           String requestBody, String responseBody, long cost) {
        int status = response.getStatus();
        String traceId = MDC.get(properties.mdcKey());
        String message = "access method={} target={} requestBody={} status={} responseBody={} "
                + "cost={}ms traceId={}";
        Object[] args = {
                request.getMethod(), target(request), requestBody, status,
                responseBody, cost, traceId
        };
        if (cost >= properties.slowThresholdMs() || status >= 500) {
            log.warn(message, args);
        } else {
            log.info(message, args);
        }
    }

    /** 渲染后的 target：path variable 与 query 参数均已脱敏，便于单测覆盖。 */
    public String target(HttpServletRequest request) {
        String uri = request.getRequestURI();
        Object pattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        Object variables = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (pattern instanceof String patternText && variables instanceof Map<?, ?> vars && !vars.isEmpty()) {
            String rendered = patternText;
            for (Map.Entry<?, ?> entry : vars.entrySet()) {
                String name = String.valueOf(entry.getKey());
                Object value = entry.getValue();
                String masked = String.valueOf(sensitiveMatcher.mask(name, value == null ? null : value.toString()));
                rendered = rendered.replace("{" + name + "}", masked);
            }
            uri = rendered;
        }
        return uri + querySuffix(request);
    }

    /** 便于单测覆盖查询参数脱敏规则。 */
    public String querySuffix(HttpServletRequest request) {
        // multipart 请求读取 parameterMap 可能提前触发解析，这里只记录 URI
        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase(Locale.ROOT).startsWith("multipart/")) {
            return "";
        }
        Map<String, String[]> params = request.getParameterMap();
        if (params == null || params.isEmpty()) {
            return "";
        }
        // 分隔符：前缀和后缀
        StringJoiner joiner = new StringJoiner("&", "?", "");
        params.forEach((name, values) -> {
            String value = values == null || values.length == 0
                    ? ""
                    : String.join(",", values);
            joiner.add(name + "=" + sensitiveMatcher.mask(name, value));
        });
        return joiner.toString();
    }

    private String readRequestBody(ContentCachingRequestWrapper request) {
        return readBody(request.getContentAsByteArray(), request.getCharacterEncoding(), request.getContentType());
    }

    private String readResponseBody(ContentCachingResponseWrapper response) {
        return readBody(response.getContentAsByteArray(), response.getCharacterEncoding(), response.getContentType());
    }

    private String readBody(byte[] content, String encoding, String contentType) {
        if (content.length == 0) {
            return "";
        }
        Charset charset = charset(encoding);
        int length = Math.min(content.length, properties.maxBodyLength());
        String body = new String(content, 0, length, charset);
        return maskBody(body, contentType);
    }

    private String maskBody(String body, String contentType) {
        String trimmed = body.trim();
        boolean json = (contentType != null && contentType.toLowerCase(Locale.ROOT).contains("json"))
                || trimmed.startsWith("{") || trimmed.startsWith("[");
        if (json) {
            try {
                JsonNode root = objectMapper.readTree(body);
                maskJsonNode(root);
                return objectMapper.writeValueAsString(root);
            } catch (IOException e) {
                // 截断可能导致 JSON 不完整，退化为文本规则兜底
                return textMasker().mask(body);
            }
        }
        return textMasker().mask(body);
    }

    private SensitiveTextMasker textMasker() {
        return new SensitiveTextMasker(sensitiveMatcher);
    }

    /** 递归按字段名脱敏 JSON 节点；数组与普通对象均可覆盖。 */
    private void maskJsonNode(JsonNode node) {
        if (node instanceof ObjectNode objectNode) {
            Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                JsonNode value = entry.getValue();
                if (value.isObject() || value.isArray()) {
                    maskJsonNode(value);
                } else if (value.isTextual()) {
                    Object masked = sensitiveMatcher.mask(entry.getKey(), value.asText());
                    objectNode.put(entry.getKey(), String.valueOf(masked));
                }
            }
        } else if (node instanceof ArrayNode arrayNode) {
            for (JsonNode child : arrayNode) {
                maskJsonNode(child);
            }
        }
    }

    private static Charset charset(String encoding) {
        if (encoding == null || encoding.isBlank()) {
            return StandardCharsets.UTF_8;
        }
        try {
            return Charset.forName(encoding);
        } catch (Exception e) {
            return StandardCharsets.UTF_8;
        }
    }
}
