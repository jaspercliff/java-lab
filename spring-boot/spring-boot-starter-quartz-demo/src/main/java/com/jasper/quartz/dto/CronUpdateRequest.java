package com.jasper.quartz.dto;

/**
 * 只修改 Cron 表达式的请求体。
 *
 * <p>对应 {@code Scheduler#rescheduleJob(TriggerKey, Trigger)}：
 * 保留原 TriggerKey，替换 schedule，不会丢失已持久化的 JobDetail。
 *
 * @param cron           新的 Cron 表达式
 * @param timeZone       新时区，不传则使用 quartz-demo.time-zone
 * @param misfirePolicy  新的 misfire 策略，默认 SMART
 */
public record CronUpdateRequest(
        String cron,
        String timeZone,
        MisfirePolicy misfirePolicy
) {
}
