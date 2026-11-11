
package com.jasper.trace.context;

import org.slf4j.MDC;

import java.util.UUID;

public class TraceIdProvider {

    private final String mdcKey;

    public TraceIdProvider(String mdcKey) {
        this.mdcKey = mdcKey;
    }

    /**
     * 获取当前线程的 TraceId
     */
    public String getTraceId() {
        return MDC.get(mdcKey);
    }

    /**
     * 生成新的 TraceId
     */
    public String generateTraceId() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "");
    }

    /**
     * 设置当前线程 TraceId
     */
    public void setTraceId(String traceId) {
        MDC.put(mdcKey, traceId);
    }

    /**
     * 清理当前线程 TraceId
     */
    public void clear() {
        MDC.remove(mdcKey);
    }
}
