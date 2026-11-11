package com.jasper.trace.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "jasper.trace")
public record TraceProperties(
        @DefaultValue("true")
        boolean enabled,

        @DefaultValue("X-Trace-Id")
        String headerName,

        @DefaultValue("true")
        boolean responseHeader,
//        是否接受外部传入的 TraceId
        @DefaultValue("false")
        boolean acceptIncoming,

        @DefaultValue("traceId")
        String mdcKey,

        @DefaultValue
        Async async
) {
    public record Async(
            @DefaultValue("true")
            boolean enabled,

            @DefaultValue("4")
            int corePoolSize,

            @DefaultValue("16")
            int maxPoolSize,

            @DefaultValue("500")
            int queueCapacity,

            @DefaultValue("60")
            int keepAliveSeconds,

            @DefaultValue("trace-async-")
            String threadNamePrefix,
//            关闭时，是否等待已经提交的任务执行完成
            @DefaultValue("true")
            boolean waitForTasksToCompleteOnShutdown,

            @DefaultValue("30")
            int awaitTerminationSeconds
    ) {}
}