package com.jasper.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

/**
 * 优雅地中断一个长任务。
 *
 * <p>关键点：
 * <ul>
 *   <li>只有实现 {@link InterruptableJob} 的 Job 才能被 {@code Scheduler#interrupt(JobKey)} 中断；
 *       普通 Job 调用 interrupt 会抛 {@link UnableToInterruptJobException}。</li>
 *   <li>Quartz 会在另一个线程调用 {@link #interrupt()}。Quartz 不会强制杀死 Java 线程——是否停止由 Job 自己决定。</li>
 *   <li>volatile 因为调用 interrupt() 和执行 execute() 的可能是不同线程，所以需要保证中断状态的可见性。</li>
 *   <li>不要用 {@code Thread.stop()}：会破坏锁和一致性。</li>
 * </ul>
 *
 * <p>测试方式：启动后等 slowJob 开始执行，调用
 * {@code POST /api/quartz/jobs/demo/slowJob/interrupt}。
 */
@Slf4j
public class SlowJob implements InterruptableJob {

    /** 中断标志：interrupt() 可能先于 execute() 的检查发生，所以用 volatile。 */
    private volatile boolean interrupted = false;

    /** 当前执行线程的引用，用于真正唤醒 sleep。 */
    private volatile Thread executingThread;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jobKey = context.getJobDetail().getKey().toString();
        int seconds = Integer.parseInt(context.getMergedJobDataMap().getString("seconds"));
        int ticks = Math.max(1, seconds * 10);

        executingThread = Thread.currentThread();
        log.info("[SlowJob] 开始执行 job={}，预计 {} 秒（每 100ms 检查一次中断）", jobKey, seconds);

        try {
            for (int i = 1; i <= ticks; i++) {
                if (interrupted) { // 实现 InterruptableJob 并不意味着 Java 线程会自动停止。任务必须主动检查中断状态
                    log.warn("[SlowJob] job={} 收到中断标志，在第 {} 个 100ms 处退出", jobKey, i);
                    return;
                }
                Thread.sleep(100);
                if (i % 50 == 0) {
                    log.info("[SlowJob] job={} 已执行 {} 秒...", jobKey, i / 10);
                }
            }
            log.info("[SlowJob] job={} 正常执行完成", jobKey);
        } catch (InterruptedException e) {
            //InterruptedException 被抛出时，JVM 会自动清除当前线程的中断标志
            // 恢复中断标记，把“被中断”的事实传递给上层
            Thread.currentThread().interrupt();
            log.warn("[SlowJob] job={} 被 Thread.interrupt() 唤醒，提前结束", jobKey);
        } finally {
            executingThread = null;
        }
    }

    /**
     * 由 Quartz 的调度线程调用(scheduler.interrupt(jobKey);)。
     *
     * <p>这里同时设置标志并中断执行线程，保证即使 Job 正阻塞在 sleep 也能立即醒来。
     */
    @Override
    public void interrupt() throws UnableToInterruptJobException {
        interrupted = true;
        Thread thread = executingThread;
        if (thread != null) {
            thread.interrupt();
        }
    }
}
