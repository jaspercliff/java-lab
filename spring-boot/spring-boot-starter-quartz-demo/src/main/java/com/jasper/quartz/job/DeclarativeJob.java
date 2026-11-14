package com.jasper.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * 演示“声明式注册”：不在代码里调用 {@code scheduler.scheduleJob}，
 * 而是把 {@code JobDetail} 和 {@code Trigger} 作为 Spring Bean 交给
 * {@code SchedulerFactoryBean} 自动注册（见 {@code DeclarativeQuartzConfig}）。
 *
 * <p>适用场景：任务固定、随应用发布，不需要运行时增删。
 * 动态任务（用户配置、后台管理）仍应使用 {@code Scheduler} API。
 */
@Slf4j
public class DeclarativeJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        log.info("[DeclarativeJob] 由 Spring Bean 声明式注册并触发，source={} fireTime={}",
                context.getMergedJobDataMap().getString("source"),
                context.getFireTime());
    }
}
