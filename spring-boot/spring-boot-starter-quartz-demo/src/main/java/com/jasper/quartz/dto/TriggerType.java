package com.jasper.quartz.dto;

/**
 * Quartz 四种常用 Trigger 类型 + “只建 Job 不建 Trigger”。
 *
 * <p>选择建议：
 * <ul>
 *   <li>{@link #SIMPLE}：固定间隔、固定次数，例如“每 10 秒执行一次，共 5 次”。</li>
 *   <li>{@link #CRON}：日历语义，例如“每天 9 点”“每月最后一天”，生产最常用。</li>
 *   <li>{@link #CALENDAR_INTERVAL}：按自然月/年递增，能正确处理“1 月 31 日 + 1 个月 = 2 月最后一天”，
 *       这是 SimpleTrigger 做不到的。</li>
 *   <li>{@link #DAILY_TIME_INTERVAL}：只在每天某个时间窗口内按间隔执行，例如“工作日 9:00-18:00 每 30 分钟”。</li>
 *   <li>{@link #NONE}：只注册 JobDetail（必须 durable），之后用 {@code triggerJob} 手动触发。</li>
 * </ul>
 */
public enum TriggerType {
    SIMPLE,
    CRON,
    CALENDAR_INTERVAL,
    DAILY_TIME_INTERVAL,
    NONE
}
