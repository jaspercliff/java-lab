package com.jasper.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * 演示 Calendar（日历排除）：
 * 该 Job 的 Cron 是“每天 09:00”，但 Trigger 通过 {@code modifiedByCalendar("cnHolidays")}
 * 绑定了节假日日历，被排除的日期不会触发。
 *
 * <p>Quartz 常用 Calendar：
 * <ul>
 *   <li>{@code HolidayCalendar}：排除整天（本例）。</li>
 *   <li>{@code CronCalendar}：用 Cron 表达式描述排除时段，例如“每天 02:00-03:00 不跑”。</li>
 *   <li>{@code DailyCalendar}：排除每天的一个时间区间，可跨天。</li>
 *   <li>{@code WeeklyCalendar} / {@code MonthlyCalendar} / {@code AnnualCalendar}：按周/月/年排除。</li>
 * </ul>
 *
 * <p>注意：
 * <ul>
 *   <li>Calendar 按名称注册到 Scheduler，Trigger 通过名称引用；它不是 Spring 的 Calendar。</li>
 *   <li>修改 Calendar 后要重新 {@code addCalendar(name, cal, true, true)} 才会生效（尤其 JDBC 模式，
 *       因为 Calendar 会被序列化进 QRTZ_CALENDARS 表）。</li>
 *   <li>Calendar 的日期判断使用 Scheduler 的默认时区，跨时区任务要特别小心。</li>
 * </ul>
 */
@Slf4j
public class CalendarJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        log.info("""
                        [CalendarJob] 今天不是节假日，执行 job={}
                          fireTime={} nextFireTime={} calendar={}""",
                context.getJobDetail().getKey(),
                context.getFireTime(),
                context.getTrigger().getNextFireTime(),
                context.getTrigger().getCalendarName());
    }
}
