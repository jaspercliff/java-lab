package com.jasper.quartz.listener;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 * 按“分组”注册的 JobListener，演示 {@code GroupMatcher}。
 *
 * <p>注册代码在 {@code QuartzBootstrap#registerMatcherListeners}：
 * <pre>{@code
 * scheduler.getListenerManager()
 *     .addJobListener(new GroupJobListener(), GroupMatcher.jobGroupEquals("demo"));
 * }</pre>
 *
 * <p>Quartz 常用 Matcher：
 * <ul>
 *   <li>{@code GroupMatcher.jobGroupEquals/StartsWith/EndsWith/Contains}</li>
 *   <li>{@code KeyMatcher.keyEquals(JobKey/TriggerKey)}</li>
 *   <li>{@code NameMatcher.nameEquals/StartsWith/EndsWith/Contains}</li>
 *   <li>{@code AndMatcher.and(a, b)} / {@code OrMatcher.or(a, b)}</li>
 *   <li>{@code EverythingMatcher.allJobs()/allTriggers()}</li>
 * </ul>
 *
 * <p>与全局 Listener 的区别：全局 Listener 对所有 Job 生效；带 Matcher 的 Listener 只对匹配的 Job 生效。
 * 同一个 Listener 可以注册多个 Matcher（OR 语义）。
 */
@Slf4j
public class GroupJobListener implements JobListener {

    @Override
    public String getName() {
        return "groupJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        log.debug("[GroupJobListener] demo 组任务开始 job={}", context.getJobDetail().getKey());
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        log.debug("[GroupJobListener] demo 组任务被否决 job={}", context.getJobDetail().getKey());
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        log.debug("[GroupJobListener] demo 组任务结束 job={} 耗时={}ms",
                context.getJobDetail().getKey(), context.getJobRunTime());
    }
}
