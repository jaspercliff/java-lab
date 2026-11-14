package com.jasper.quartz.dto;

import java.util.List;
import java.util.Map;

/**
 * Job 的只读视图。
 *
 * @param concurrentExecutionDisallowed 是否标注了 {@code @DisallowConcurrentExecution}
 * @param persistJobDataAfterExecution  是否标注了 {@code @PersistJobDataAfterExecution}
 * @param requestsRecovery              是否请求故障恢复
 * @param jobData                       当前 JobDetail 上的 JobDataMap（已做 JSON 安全转换）
 * @param triggers                      该 Job 关联的所有 Trigger
 */
public record JobView(
        String name,
        String group,
        String description,
        String jobClass,
        boolean durable,
        boolean concurrentExecutionDisallowed,
        boolean persistJobDataAfterExecution,
        boolean requestsRecovery,
        Map<String, Object> jobData,
        List<TriggerView> triggers
) {
}
