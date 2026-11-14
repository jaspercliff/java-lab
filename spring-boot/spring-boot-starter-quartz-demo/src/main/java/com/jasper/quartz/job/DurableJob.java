package com.jasper.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * 演示 Durable Job：没有 Trigger 也长期存在于 JobStore 中。
 *
 * <p>普通 Job 一旦没有任何 Trigger，会被 Quartz 自动删除。
 * 调用 {@code JobBuilder#storeDurably()} 后，即使没有 Trigger 也会保留，
 * 之后可以随时 {@code scheduler.triggerJob(jobKey, data)} 手动执行。
 *
 * <p>典型用途：
 * <ul>
 *   <li>“手动触发的批处理任务”，不想让它按时间自动跑；</li>
 *   <li>作为模板 Job，运行时再动态挂 Trigger；</li>
 *   <li>配合管理后台，让运维手动补数据。</li>
 * </ul>
 *
 * <p>注意：durable Job 没有 Trigger 时，{@code Scheduler#getTriggersOfJob} 返回空列表，
 * 这是正常的，不是注册失败。
 */
@Slf4j
public class DurableJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        String note = context.getMergedJobDataMap().getString("note");
        log.info("[DurableJob] 手动触发执行 job={} note={} fireTime={}",
                context.getJobDetail().getKey(), note, context.getFireTime());
        context.setResult("durable-job-executed");
    }
}
