package com.jasper.quartz.dto;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Cron 表达式预览结果。
 *
 * <p>利用 {@code CronExpression#getNextValidTimeAfter(Date)} 计算后续触发时间，
 * 上线前可用它验证表达式是否符合预期（尤其是时区、跨月、夏令时）。
 * <br>
 *夏令时（DST，Daylight Saving Time），简单来说就是：在一年中的部分月份，人为把时钟拨快 1 小时，让人们在夏季能够更多地利用白天的自然光。
 * <br>
 * 通常：
 * - 进入夏令时：时钟拨快 1 小时。
 * - 退出夏令时：时钟拨回 1 小时。
 */
public record CronPreview(
        String expression,
        String timeZone,
        List<ZonedDateTime> nextFireTimes
) {
}
