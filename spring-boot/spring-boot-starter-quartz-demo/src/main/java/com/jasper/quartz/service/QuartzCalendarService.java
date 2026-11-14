package com.jasper.quartz.service;

import com.jasper.quartz.config.QuartzDemoProperties;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.calendar.HolidayCalendar;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * 节假日 Calendar 的运行时维护。
 *
 * <p>关键点：Calendar 是 Scheduler 级别的对象，不是 Spring Bean 的普通数据。
 * 修改 {@link HolidayCalendar} 后必须重新注册：
 * <pre>{@code
 * scheduler.addCalendar(name, calendar, true, true);
 * }</pre>
 * 第三个参数 replace=true 表示覆盖同名 Calendar；
 * 第四个参数 updateTriggers=true 表示让引用该 Calendar 的 Trigger 重新计算 nextFireTime。
 *
 * <p>JDBC 模式下 Calendar 会被序列化到 {@code QRTZ_CALENDARS} 表，
 * 所以“改了内存对象但不重新注册”在重启后一定会丢，在运行中也可能不生效。
 */
@Service
public class QuartzCalendarService {

    private final Scheduler scheduler;
    private final HolidayCalendar holidayCalendar;
    private final String calendarName;

    public QuartzCalendarService(Scheduler scheduler,
                                 @Qualifier("cnHolidays") HolidayCalendar holidayCalendar,
                                 QuartzDemoProperties properties) {
        this.scheduler = scheduler;
        this.holidayCalendar = holidayCalendar;
        this.calendarName = properties.holidayCalendar();
    }

    /**
     * 当前被排除的日期（UTC Instant 表示，按 JVM 默认时区取当天 00:00）。
     */
    public List<Instant> excludedDates() {
        return holidayCalendar.getExcludedDates().stream()
                .map(Date::toInstant)
                .toList();
    }

    /**
     * 添加一个节假日，并立即让相关 Trigger 重新计算下一次触发时间。
     */
    public List<Instant> addHoliday(LocalDate date) throws SchedulerException {
        holidayCalendar.addExcludedDate(toDate(date));
        reRegister();
        return excludedDates();
    }

    /**
     * 移除一个节假日。
     */
    public List<Instant> removeHoliday(LocalDate date) throws SchedulerException {
        holidayCalendar.removeExcludedDate(toDate(date));
        reRegister();
        return excludedDates();
    }

    private void reRegister() throws SchedulerException {
        scheduler.addCalendar(calendarName, holidayCalendar, true, true);
    }

    private Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
