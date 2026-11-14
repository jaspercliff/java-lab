package com.jasper.quartz.config;

import com.jasper.quartz.listener.GlobalJobListener;
import com.jasper.quartz.listener.GlobalSchedulerListener;
import com.jasper.quartz.listener.GlobalTriggerListener;
import org.quartz.impl.calendar.HolidayCalendar;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Quartz 与 Spring 的集成配置。
 *
 * <p>Spring Boot 的 {@code QuartzAutoConfiguration} 已经替我们做了很多事：
 * <ul>
 *   <li>创建 {@code SchedulerFactoryBean} 和 {@code Scheduler}；</li>
 *   <li>根据 {@code spring.quartz.job-store-type} 选择 RAMJobStore / JDBCJobStore；</li>
 *   <li>设置 {@code SpringBeanJobFactory}，让 Job 支持 Spring 注入；</li>
 *   <li>把容器里的 {@code JobDetail}、{@code Trigger}、{@code Calendar} Bean 自动注册到 Scheduler；</li>
 *   <li>应用 {@code spring.quartz.properties.*} 到 Quartz。</li>
 * </ul>
 *
 * <p>本类只做三件“自动配置不覆盖”的事：
 * <ol>
 *   <li>用自定义 JobFactory 替换默认的，增加日志；</li>
 *   <li>注册全局 Job/Trigger/Scheduler Listener；</li>
 *   <li>注册节假日 Calendar Bean（Spring Boot 会自动收集 {@code Map<String, Calendar>}）。</li>
 * </ol>
 *
 * <p>注意：{@code SchedulerFactoryBeanCustomizer} 的执行时机在 Scheduler 创建之前，
 * 所以这里设置 JobFactory/Listener 才能生效。
 */
@Configuration(proxyBeanMethods = false)
public class QuartzConfig {

    /**
     * 自定义 JobFactory，见 {@link TracingJobFactory}。
     */
    @Bean
    public TracingJobFactory tracingJobFactory(ApplicationContext applicationContext) {
        return new TracingJobFactory(applicationContext);
    }

    /**
     * 通过 Spring Boot 提供的扩展点定制 SchedulerFactoryBean。
     *
     * <p>这里注册的是“全局 Listener”，对所有 Job/Trigger 生效。
     * 按分组/按 Key 的 Listener 在 {@code QuartzBootstrap} 中用 ListenerManager 动态注册。
     */
    @Bean
    public SchedulerFactoryBeanCustomizer quartzCustomizer(
            TracingJobFactory jobFactory,
            GlobalJobListener globalJobListener,
            GlobalTriggerListener globalTriggerListener,
            GlobalSchedulerListener globalSchedulerListener) {

        return schedulerFactoryBean -> {
            // 替换默认 SpringBeanJobFactory，保留注入能力并增加实例创建日志
            schedulerFactoryBean.setJobFactory(jobFactory);

            // 全局监听器：所有 Job/Trigger 都会经过它们
            schedulerFactoryBean.setGlobalJobListeners(globalJobListener);
            schedulerFactoryBean.setGlobalTriggerListeners(globalTriggerListener);
            schedulerFactoryBean.setSchedulerListeners(globalSchedulerListener);
        };
    }

    /**
     * 节假日 Calendar。
     *
     * <p>Spring Boot 会注入 {@code Map<String, Calendar>}，key 就是 Bean 名称，
     * 并调用 {@code SchedulerFactoryBean#setCalendars} 注册到 Scheduler。
     * 因此 Bean 名称必须与 Trigger 上 {@code modifiedByCalendar("cnHolidays")} 一致。
     *
     * <p>这里预置了三个示例节假日；运行时可以通过
     * {@code POST /api/quartz/calendars/cnHolidays/holidays?date=2026-12-25} 动态添加。
     *
     * <p>注意：HolidayCalendar 排除的是“整天”，日期按 JVM 默认时区解释。
     */
    @Bean(name = "cnHolidays")
    public HolidayCalendar cnHolidays() {
        HolidayCalendar calendar = new HolidayCalendar();
        ZoneId zone = ZoneId.systemDefault();

        // 示例节假日：元旦、国庆
        calendar.addExcludedDate(toDate(LocalDate.of(2026, 1, 1), zone));
        calendar.addExcludedDate(toDate(LocalDate.of(2026, 10, 1), zone));
        calendar.addExcludedDate(toDate(LocalDate.of(2026, 11, 13), zone));
        return calendar;
    }

    private Date toDate(LocalDate date, ZoneId zone) {
        return Date.from(date.atStartOfDay(zone).toInstant());
    }
}
