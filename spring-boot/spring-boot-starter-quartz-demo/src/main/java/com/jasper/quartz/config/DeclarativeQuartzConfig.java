package com.jasper.quartz.config;

import com.jasper.quartz.job.DeclarativeJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.JobBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 声明式注册：把 JobDetail 和 Trigger 作为 Spring Bean 交给 SchedulerFactoryBean。
 * 适合静态任务注册
 * <p>原理：
 * {@code QuartzAutoConfiguration} 会注入 {@code ObjectProvider<JobDetail>} 和
 * {@code ObjectProvider<Trigger>}，然后分别调用
 * {@code SchedulerFactoryBean#setJobDetails(...)} / {@code setTriggers(...)}；
 * Scheduler 初始化时自动完成注册。
 *
 * <p>与编程式（{@code scheduler.scheduleJob(...)}）的取舍：
 * <ul>
 *   <li>声明式：任务固定、随代码发布、配置集中，适合“系统内置任务”；</li>
 *   <li>编程式：任务由用户/后台动态创建，适合“业务可配置任务”；</li>
 *   <li>两者可以混用，但要注意 JobKey 不要冲突。</li>
 * </ul>
 *
 * <p>持久化注意：JDBC JobStore 下如果同名 Job 已存在，启动时可能抛
 * {@code ObjectAlreadyExistsException}。可设置
 * {@code spring.quartz.overwrite-existing-jobs=true} 让 Spring 覆盖 JobDetail。
 */
@Configuration(proxyBeanMethods = false)
public class DeclarativeQuartzConfig {

    /**
     * 声明式 JobDetail。
     *
     * <p>{@code storeDurably()} 让 Job 在没有 Trigger 时也不会被删除；
     * 这里虽然马上会绑定 Trigger，但保留 durable 更安全，也便于后续动态加 Trigger。
     */
    @Bean
    public JobDetail declarativeJobDetail() {
        return JobBuilder.newJob(DeclarativeJob.class)
                .withIdentity("declarativeJob", "declarative")
                .withDescription("通过 Spring @Bean 声明式注册的 JobDetail")
                .usingJobData("source", "spring-bean")
                .storeDurably()
                .build();
    }

    /**
     * 声明式 Trigger：每 2 分钟执行一次。
     *
     * <p>参数 {@code declarativeJobDetail} 会由 Spring 按类型注入上面那个 Bean。
     * 使用 {@code forJob(jobDetail)} 而不是 {@code forJob(jobKey)}，
     * 可以保证 Trigger 与 JobDetail 的关联在启动阶段就确定。
     *
     * <p>{@code withMisfireHandlingInstructionDoNothing()}：
     * 应用停机期间错过的执行不补跑，恢复后等下一个整 2 分钟。
     * 对报表/对账类任务，这通常比“补跑所有历史”更安全。
     */
    @Bean
    public Trigger declarativeJobTrigger(
            @Qualifier("declarativeJobDetail") JobDetail declarativeJobDetail) {

        return TriggerBuilder.newTrigger()
                .withIdentity("declarativeJob-trigger", "declarative")
                .withDescription("每 2 分钟执行一次（声明式）")
                .forJob(declarativeJobDetail)
                .withPriority(5)
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/2 * * * ?")
                        .withMisfireHandlingInstructionDoNothing())
                .build();
    }
}
