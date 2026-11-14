package com.jasper.quartz.dto;

import java.util.Map;

/**
 * 创建/更新任务的请求体。
 *
 * <p>所有字段都是可选的（name、jobClass 除外），服务端会补默认值：
 * <ul>
 *   <li>group 默认 {@code quartz-demo.group}（demo）</li>
 *   <li>triggerName 默认 {@code name + "-trigger"}</li>
 *   <li>triggerType 默认 SIMPLE，intervalSeconds 默认 60，repeatCount 默认 -1（永久）</li>
 *   <li>priority 默认 5（Quartz 默认也是 5，值越大越先执行）</li>
 *   <li>misfirePolicy 默认 SMART</li>
 * </ul>
 *
 * <p>时间字段使用 ISO-8601 带时区的 Instant 字符串，例如：
 * {@code 2026-11-11T13:00:00Z}、{@code 2026-11-11T21:00:00+08:00}。
 *
 * <p>关于 data（JobDataMap）：
 * <ul>
 *   <li>JDBC JobStore 且 {@code useProperties=false} 时，值必须可序列化（String/Number/Boolean 最安全）。</li>
 *   <li>若开启 {@code org.quartz.jobStore.useProperties=true}，则所有值必须是 String，可规避序列化漏洞。</li>
 *   <li>Trigger 上的同名 key 会覆盖 JobDetail 上的值，见 {@code JobExecutionContext#getMergedJobDataMap()}。</li>
 * </ul>
 *
 * @param name            任务名（必填），与 group 组成 JobKey
 * @param group           任务分组，默认 demo
 * @param jobClass        Job 实现类的全限定名（必填），必须实现 org.quartz.Job
 * @param description     描述，便于运维排查
 * @param triggerName     触发器名，默认 name + "-trigger"
 * @param triggerGroup    触发器分组，默认与 job 相同
 * @param triggerType     触发器类型，默认 SIMPLE；NONE 表示只注册 durable Job
 * @param cron            CRON 类型必填，支持 6/7 段 Quartz 表达式
 * @param timeZone        Cron 时区，默认 quartz-demo.time-zone
 * @param intervalSeconds 间隔秒数，SIMPLE/CALENDAR_INTERVAL/DAILY_TIME_INTERVAL 使用
 * @param repeatCount     SIMPLE 重复次数，-1 表示永久；0 表示只执行一次
 * @param startAt         首次触发时间（ISO Instant），不传则立即开始
 * @param endAt           结束时间（ISO Instant），不传则永不结束
 * @param priority        触发器优先级，默认 5
 * @param calendar        绑定的 Quartz Calendar 名称，例如 cnHolidays
 * @param data            写入 JobDataMap 的初始数据
 * @param durable         Job 是否持久保留（无 Trigger 时必须为 true）
 * @param requestRecovery 宕机/故障转移后是否恢复执行（需要 JDBC JobStore）
 * @param misfirePolicy   misfire 策略，默认 SMART
 * @param replace         已存在同名 Job/Trigger 时是否覆盖
 */
public record JobRequest(
        String name,
        String group,
        String jobClass,
        String description,
        String triggerName,
        String triggerGroup,
        TriggerType triggerType,
        String cron,
        String timeZone,
        Integer intervalSeconds,
        Integer repeatCount,
        String startAt,
        String endAt,
        Integer priority,
        String calendar,
        Map<String, Object> data,
        Boolean durable,
        Boolean requestRecovery,
        MisfirePolicy misfirePolicy,
        Boolean replace
) {
}
