package com.jasper.quartz;

import com.jasper.quartz.config.QuartzDemoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Quartz 全量实用知识点演示入口。
 *
 * <p>本模块刻意把“一个知识点一个类/一个方法”拆开，方便对照学习：
 * <ul>
 *   <li>Job / JobDetail / JobDataMap / JobExecutionContext：{@code com.jasper.quartz.job}</li>
 *   <li>Simple / Cron / CalendarInterval / DailyTimeInterval Trigger：{@code QuartzJobService#buildTrigger}</li>
 *   <li>Listener（全局、分组、指定 Key、Scheduler 生命周期）：{@code com.jasper.quartz.listener}</li>
 *   <li>Calendar（节假日排除）：{@code QuartzConfig#cnHolidays}、{@code QuartzCalendarService}</li>
 *   <li>Misfire（错失触发）策略：{@code MisfirePolicy} 与各 ScheduleBuilder</li>
 *   <li>动态增删改查、暂停恢复、立即执行、中断：{@code QuartzJobService} + REST 接口</li>
 *   <li>Spring Bean 注入 Job、声明式 JobDetail/Trigger：{@code QuartzConfig}、{@code DeclarativeQuartzConfig}</li>
 *   <li>RAMJobStore / JDBCJobStore / 集群：{@code application.yml}、{@code application-jdbc.yml}</li>
 * </ul>
 *
 * <p>启动后访问 {@code http://localhost:8090/api/quartz/scheduler} 查看调度器状态。
 * 更完整的知识点地图、curl 示例、Cron 速查和生产实践见模块根目录 README.md。
 */
@SpringBootApplication
@EnableConfigurationProperties(QuartzDemoProperties.class)
public class QuartzDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuartzDemoApplication.class, args);
    }
}
