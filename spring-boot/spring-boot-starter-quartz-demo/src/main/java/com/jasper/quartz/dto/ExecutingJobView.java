package com.jasper.quartz.dto;

import java.time.Instant;

/**
 * 当前正在执行的 Job 快照。
 *
 * <p>对应 {@code Scheduler#getCurrentlyExecutingJobs()}。
 * 注意：RAMJobStore 只返回本实例正在执行的任务；集群中其他节点的执行情况需要查
 * {@code QRTZ_FIRED_TRIGGERS} 表或接入统一监控。
 */
public record ExecutingJobView(
        String job,
        String trigger,
        String fireInstanceId,
        Instant fireTime
) {
}
