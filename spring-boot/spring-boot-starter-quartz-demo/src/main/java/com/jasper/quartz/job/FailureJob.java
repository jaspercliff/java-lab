package com.jasper.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 演示 Job 失败后的控制手段：{@link JobExecutionException}。
 *
 * <p>Job 内抛出的异常不会让调度器崩溃，但会导致：
 * <ul>
 *   <li>本次执行记为失败，{@code JobListener#jobWasExecuted} 收到该异常；</li>
 *   <li>Trigger 默认继续按计划执行（除非你设置下面的指令）。</li>
 * </ul>
 *
 * <p>三个常用指令：
 * <ul>
 *   <li>{@code setRefireImmediately(true)}：立即重跑当前 Job（不等待下一次计划时间）。
 *       必须自己限制次数，否则会死循环。本类用 refireCount 限制。</li>
 *   <li>{@code setUnscheduleAllTriggers(true)}：失败后删除该 Job 的所有 Trigger。</li>
 *   <li>{@code setUnscheduleFiringTrigger(true)}：只删除当前 Trigger，保留其他 Trigger。</li>
 * </ul>
 *
 * <p>“失败重试”更工程化的做法：不要在 Job 里无限重试，而是把失败事件投递到 MQ/延迟队列，
 * 或用单独的“重试 Job + 退避时间”实现，避免占满 Quartz 线程池。
 */
@Slf4j
public class FailureJob implements Job {

    @Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {

        JobDataMap data = context.getMergedJobDataMap();

        int maxRetries = Math.max(0, Integer.parseInt(data.getString("failTimes")));
        int refireCount = context.getRefireCount();

        try {
            // 1. 执行业务逻辑
            doBusiness(context);

            // 2. 执行成功，正常返回
            log.info("[FailureJob] job={} 执行成功，refireCount={}",
                    context.getJobDetail().getKey(), refireCount);

        } catch (Exception e) {

            // 3. 还有重试机会，立即重试
            if (refireCount < maxRetries) {

                log.warn("[FailureJob] job={} 执行失败，准备立即重试，当前重试次数={}/{}",
                        context.getJobDetail().getKey(),
                        refireCount,
                        maxRetries,
                        e);

                JobExecutionException failure =
                        new JobExecutionException(e);

                failure.setRefireImmediately(true);

                throw failure;
            }

            // 4. 重试次数耗尽，删除当前 Trigger
            log.error("[FailureJob] job={} 重试次数耗尽，删除当前 Trigger",
                    context.getJobDetail().getKey(), e);

            JobExecutionException failure =
                    new JobExecutionException(e);

            failure.setUnscheduleFiringTrigger(true);

            throw failure;
        }
    }

    /**
     * 模拟业务逻辑
     */
    private void doBusiness(JobExecutionContext context) {

        JobDataMap data = context.getMergedJobDataMap();

        int failBusinessTimes = Integer.parseInt(data.getString("businessFailTimes"));

        int refireCount = context.getRefireCount();

        // 模拟前几次业务执行失败
        if (refireCount < failBusinessTimes) {
            throw new RuntimeException("模拟业务执行失败");
        }

        // 真正的业务逻辑
        log.info("[FailureJob] 业务处理成功");
    }
}
