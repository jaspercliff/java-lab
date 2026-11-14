package com.jasper.quartz.dto;

/**
 * Misfire（错失触发）策略的统一抽象。
 *
 * <p>什么是 misfire？
 * 触发器到了该执行的时间，但调度器没来得及执行（宕机、线程池打满、被暂停、系统时间跳跃等），
 * 恢复后 Quartz 需要决定“错过的这些次数怎么办”。这个决定就是 misfire 策略。
 *
 * <p>注意：<b>不同 Trigger 支持的策略不同</b>，不要用一个枚举硬套所有类型：
 * <ul>
 *   <li>SimpleTrigger 支持：IGNORE、FIRE_NOW、NEXT_WITH_*、NOW_WITH_*、SMART。</li>
 *   <li>CronTrigger / CalendarIntervalTrigger / DailyTimeIntervalTrigger 支持：
 *       IGNORE、FIRE_NOW（= FireAndProceed）、DO_NOTHING、SMART。</li>
 * </ul>
 *
 * <p>本枚举在 {@code QuartzJobService} 中按 Trigger 类型翻译成对应的 ScheduleBuilder 方法，
 * 若类型不支持会抛出明确的 IllegalArgumentException。
 */
public enum MisfirePolicy {

    /** 交给 Quartz 的默认智能策略（不显式设置 misfireInstruction）。 */
    SMART,

    /** 忽略 misfire 策略，把所有错过的触发都补跑一遍。慎用：可能瞬间打满线程池。 */
    IGNORE,

    /** 立即补触发一次，然后按原计划继续。 */
    FIRE_NOW,

    /** 什么都不做，直接跳到下一个该触发的时间点。Cron 场景最常用（避免补跑历史任务）。 */
    DO_NOTHING,

    /** Simple 专用：保持剩余次数不变，从下一个计划时间点继续。 */
    NEXT_WITH_REMAINING_COUNT,

    /** Simple 专用：保持总次数不变，从下一个计划时间点继续。 */
    NEXT_WITH_EXISTING_COUNT,

    /** Simple 专用：立即补触发，剩余次数随之减少。 */
    NOW_WITH_REMAINING_COUNT,

    /** Simple 专用：立即补触发，总次数保持不变。 */
    NOW_WITH_EXISTING_COUNT
}
