package com.jasper.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 最基础的 Job：演示 Job / JobDetail / Trigger / JobDataMap / JobExecutionContext。
 * <p>config see QuartzBootstrap</p>
 * <p>核心概念：
 * <ul>
 *   <li><b>Job</b> 只是“做什么”，无状态；Quartz 每次触发都会 new 一个新实例。</li>
 *   <li><b>JobDetail</b> 是“谁、用什么数据做”，有 JobKey（name + group），会被持久化。</li>
 *   <li><b>Trigger</b> 是“什么时候做”，一个 JobDetail 可以绑定多个 Trigger。</li>
 *   <li><b>JobDataMap</b> 挂在 JobDetail 或 Trigger 上；{@code getMergedJobDataMap()} 返回两者合并后的视图，
 *       Trigger 上的同名 key 优先。</li>
 * </ul>
 *
 * <p>本类没有加 {@code @DisallowConcurrentExecution}，因此同一 JobKey 的多次触发可以并发执行。
 * 如果 Job 有共享状态或不能并发，必须加该注解（见 {@link CounterJob}）。
 */
@Slf4j
public class HelloWorldJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 1) JobKey：name + group，全局唯一标识一个 JobDetail
        // demo.HelloJob
        var jobKey = context.getJobDetail().getKey();

        // 2) 合并后的 JobDataMap：JobDetail 的数据 + 本次 Trigger 的数据
        JobDataMap merged = context.getMergedJobDataMap();

        // 3) 时间语义：
        //    fireTime          = 实际开始执行的时间
        //    scheduledFireTime = 原计划触发的时间（misfire 时两者会明显不同）
        //    nextFireTime      = 本次执行后下一次计划时间（由 Trigger 计算）
        log.info("""
                        [HelloWorldJob] job={} trigger={}
                          fireTime={} scheduledFireTime={} refireCount={}
                          merged.message={} jobDetail.message={}
                          triggerData.triggerMessage={} jobLevelNumber={}
                          nextFireTime={} recovering={}""",
                jobKey,
                context.getTrigger().getKey(),
                context.getFireTime(),
                context.getScheduledFireTime(),
                /*
                 *当前 Job 本次执行已经重新触发（Refire）的次数 see also {failureJob}
                 *job 执行过程中发生异常，可以通过 JobExecutionException 告诉 Quartz：不要等下一次 Trigger 触发，立即重新执行当前 Job
                 */
                context.getRefireCount(),
                // merged 中同 key 取 Trigger 的值；下一行单独打印 JobDetail 的值做对比
                merged.getString("message"),
                context.getJobDetail().getJobDataMap().getString("message"),
                context.getTrigger().getJobDataMap().getString("triggerMessage"),
                merged.getIntValue("jobLevelNumber"),
                context.getTrigger().getNextFireTime(),
                context.isRecovering());

        // 4) setResult 会出现在 JobListener 的 jobWasExecuted 中，也可用于执行结果审计。
        //    注意：结果只存在于本次 JobExecutionContext，不会持久化。
        context.setResult("HelloWorldJob-ok-" + context.getFireTime().getTime());
    }
}
