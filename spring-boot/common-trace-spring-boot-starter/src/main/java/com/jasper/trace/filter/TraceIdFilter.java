
package com.jasper.trace.filter;

import com.jasper.trace.config.TraceProperties;
import com.jasper.trace.context.TraceIdProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Pattern;

public class TraceIdFilter extends OncePerRequestFilter {

    private static final String REQUEST_ATTRIBUTE =
            TraceIdFilter.class.getName() + ".TRACE_ID";

    // 32 位十六进制字符串 只允许0-9 a-f A-F
    private static final Pattern TRACE_ID_PATTERN =
            Pattern.compile("^[0-9a-fA-F]{32}$");
    // 通常被视为“无效的”或“未采样/未追踪的”占位符 防止上游传错
    private static final String ZERO_TRACE_ID =
            "00000000000000000000000000000000";

    private final TraceProperties properties;

    private final TraceIdProvider traceIdProvider;

    public TraceIdFilter(
            TraceProperties properties,
            TraceIdProvider traceIdProvider) {
        this.properties = properties;
        this.traceIdProvider = traceIdProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
//       cache 确保同一次请求的多次 Dispatch 严格复用同一个 TraceId，不会在异步切线程时重新生成
        String traceId = (String) request.getAttribute(REQUEST_ATTRIBUTE);

        // 同一次请求的异步 Dispatch 复用原 TraceId
        if (traceId == null) {
            traceId = resolveTraceId(request);

            request.setAttribute(REQUEST_ATTRIBUTE, traceId);
        }

        String mdcKey = properties.mdcKey();
        // 保存线程原有 MDC 值
        String previousTraceId = MDC.get(mdcKey);

        try {
            MDC.put(mdcKey, traceId);

            if (properties.responseHeader() && !response.isCommitted()) {
                response.setHeader(
                        properties.headerName(),
                        traceId
                );
            }

            filterChain.doFilter(request, response);

        } finally {
            // 只恢复自己管理的 MDC Key
            if (previousTraceId == null) {
                MDC.remove(mdcKey);
                // 有值就恢复为原来的值
            } else {
                MDC.put(mdcKey, previousTraceId);
            }
        }
    }

    /**
     * 是否接受外部传入 ID 由配置控制。
     * 外部 ID 必须校验，不能直接信任请求头
     */
    private String resolveTraceId(HttpServletRequest request) {

        if (properties.acceptIncoming()) {

            String incoming = request.getHeader(properties.headerName());

            if (isValidTraceId(incoming)) {
                return incoming.toLowerCase(Locale.ROOT);
            }
        }

        return traceIdProvider.generateTraceId();
    }

    private boolean isValidTraceId(String traceId) {
        return traceId != null
                && TRACE_ID_PATTERN.matcher(traceId).matches()
                && !ZERO_TRACE_ID.equalsIgnoreCase(traceId);
    }

    /**
     * async 调用时，Spring 会在原请求线程中先执行一次 FilterChain.doFilter()，
     * 然后再切换到异步线程中再次执行一次 FilterChain.doFilter()。
     */
    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }
}
