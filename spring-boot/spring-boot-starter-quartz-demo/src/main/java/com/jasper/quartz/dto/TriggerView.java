package com.jasper.quartz.dto;

import java.time.Instant;

/**
 * Trigger 的只读视图，用于 REST 返回。
 *
 * @param state              Trigger 状态：NORMAL / PAUSED / COMPLETE / ERROR / BLOCKED / NONE
 * @param misfireInstruction Quartz 内部的 misfire 指令数字，便于对照源码
 * @param cronExpression     仅 CronTrigger 有值
 * @param timeZone           仅 CronTrigger 有值
 * @param repeatCount        仅 SimpleTrigger 有值；-1 表示永久
 * @param timesTriggered     仅 SimpleTrigger 有值
 * @param interval           仅 CalendarInterval/DailyTimeInterval 有值，人类可读描述
 */
public record TriggerView(
        String name,
        String group,
        String type,
        String state,
        Instant startTime,
        Instant endTime,
        Instant nextFireTime,
        Instant previousFireTime,
        int priority,
        String calendarName,
        int misfireInstruction,
        String cronExpression,
        String timeZone,
        Long repeatCount,
        Long timesTriggered,
        String interval
) {
}
