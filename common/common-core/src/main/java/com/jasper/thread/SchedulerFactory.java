package com.jasper.thread;

import com.jasper.constants.ThreadType;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.ReentrantLock;

public final class SchedulerFactory {
    private static  final class Holder {
        private static final SchedulerFactory INSTANCE = new SchedulerFactory();
    }
    public static SchedulerFactory getInstance() {
        return Holder.INSTANCE;
    }

    /** cached scheduler (may be {@code null} until first access) */
    private ScheduledExecutorService scheduler;
    /** remembers whether the cached scheduler was created for virtual threads */
    private boolean virtual;
    /** ReentrantLock to protect the scheduler state - virtual thread compatible */
    private final ReentrantLock lock = new ReentrantLock();

    private SchedulerFactory() { }


    @SuppressWarnings("ConstantConditions")
    public ScheduledExecutorService getScheduler() {
        ScheduledExecutorService old = null; // 保存准备被替换的旧调度器
        ScheduledExecutorService result; // 保存本次调用最终返回的调度器

        lock.lock();
        try {
            boolean desiredVirtual = ExecutorServiceFactory.getThreadType() == ThreadType.VIRTUAL;

            if (scheduler == null  //首次调用，或者执行过 reset()，当前没有缓存的调度器
                    || desiredVirtual != virtual // 当前配置要求的线程类型与已创建的调度器不一致，需要重建
                    || scheduler.isShutdown() // 调度器已收到关闭请求，不能再正常接受新的调度任务
                    || scheduler.isTerminated()) { //调度器已经完全终止，需要创建新的调度器才能继续使用

                ThreadType desiredType = desiredVirtual ? ThreadType.VIRTUAL : ThreadType.PLATFORM;
                ScheduledExecutorService fresh =
                        ExecutorServiceFactory.newSingleThreadScheduledExecutor(
                                "CircuitBreakerAutoTransitionThread", desiredType);
// replace cache
                old = scheduler;
                scheduler = fresh;
                virtual = desiredVirtual;
            }
            result = scheduler;
        } finally {
            lock.unlock();
        }

        // 锁外调用 shutdownNow()，避免在持有锁时执行关闭操作 关闭旧调度器时一直占用 lock，阻止其他线程进入临界区
        if (old != null) {
            // 一个线程拿到旧调度器的引用后，另一个线程可能立即替换并关闭它。因此，拿到调度器引用不代表后续提交的任务一定成功
            // 一般不要运行时切换线程类型
            old.shutdownNow();
        }
        return result;
    }

    /**
     * For test-code: shut down and forget the current scheduler so that the next
     * {@link #getScheduler()} call creates a fresh executor according to the then
     * active configuration.
     */
    public void reset() {
        ScheduledExecutorService old;

        lock.lock();
        try {
            old = scheduler;
            scheduler = null;
        } finally {
            lock.unlock();
        }

        if (old != null) {
            old.shutdownNow();
        }
    }
}
