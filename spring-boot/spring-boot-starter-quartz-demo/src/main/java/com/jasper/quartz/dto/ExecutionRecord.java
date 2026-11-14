package com.jasper.quartz.dto;

import java.time.Instant;

/**
 * 一次 Job 执行的历史记录，由 {@code GlobalJobListener} 写入内存环形队列。
 *
 * <p>生产环境应替换为落库/写入 Prometheus/Micrometer/ELK；
 * 这里用内存实现是为了让 Listener 的知识点可观测、可调用。
 *
 * @param fireTime          实际开始执行时间
 * @param scheduledFireTime 原计划触发时间；发生 misfire 时两者差值就是延迟
 * @param durationMs        本次执行耗时（{@code JobExecutionContext#getJobRunTime()}）
 * @param success           是否正常返回
 * @param error             异常信息（失败时）
 * @param result            Job 通过 {@code context.setResult(...)} 写入的结果
 * @param refireCount       立即重试次数（{@code JobExecutionException#setRefireImmediately}）
 */
public record ExecutionRecord(
        String job,
        String trigger,
        Instant fireTime,
        Instant scheduledFireTime,
        long durationMs,
        boolean success,
        String error,
        String result,
        int refireCount
) {
}
