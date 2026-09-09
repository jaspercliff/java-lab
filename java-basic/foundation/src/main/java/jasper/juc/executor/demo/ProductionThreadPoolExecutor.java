package jasper.juc.executor.demo;

import jasper.juc.executor.NamedThreadFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author jasper
 * @since 2026-09-09 <br>
 */
@Slf4j
public class ProductionThreadPoolExecutor extends ThreadPoolExecutor {

    @Getter private final String poolName;

    public ProductionThreadPoolExecutor(
            String poolName,
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            RejectedExecutionHandler handler) {
        super(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                new NamedThreadFactory(poolName),
                handler);
        this.poolName = poolName;
    }

    // 重写 execute，包装 Runnable
    @Override
    public void execute(@NonNull Runnable command) {
        super.execute(new TaskWrapper(command));
    }

    @Override
    public <T> Future<T> submit(@NonNull Runnable task, T result) {
        return super.submit(wrapRunnable(task), result);
    }

    @Override
    public <T> Future<T> submit(@NonNull Callable<T> task) {
        return super.submit(wrapCallable(task));
    }

    /**
     * 装饰 Runnable：传递 MDC + 异常兜底日志
     */
    private Runnable wrapRunnable(Runnable task) {
        // 1. 提交时：捕获主线程的 MDC 上下文
        final Map<String, String> contextMap = MDC.getCopyOfContextMap();

        return () -> {
            // 2. 执行时：恢复 MDC 上下文
            if (contextMap != null) {
                MDC.setContextMap(contextMap);
            }
            try {
                task.run();
            } catch (RuntimeException | Error e) {
                // 3. 异常兜底：打印日志，防止异常被 Future 吞没
                log.error("[ThreadPool] Runnable task execute failed, thread: {}",
                        Thread.currentThread().getName(), e);
                throw e; // 必须继续抛出，保证 future.get() 能拿到异常
            } catch (Throwable t) {
                log.error("[ThreadPool] Runnable task execute failed with Throwable", t);
                throw new RuntimeException(t); // 包装为 RuntimeException 抛出
            } finally {
                // 4. 清理 MDC：防止线程复用时上下文污染
                MDC.clear();
            }
        };
    }

    /**
     * 装饰 Callable：传递 MDC + 异常兜底日志 + 泛型返回值处理
     */
    private <V> Callable<V> wrapCallable(Callable<V> task) {
        final Map<String, String> contextMap = MDC.getCopyOfContextMap();

        return () -> {
            if (contextMap != null) {
                MDC.setContextMap(contextMap);
            }
            try {
                return task.call();
            } catch (Exception e) {
                // Callable.call() 允许抛出 Exception，直接捕获并打印
                log.error("[ThreadPool] Callable task execute failed, thread: {}",
                        Thread.currentThread().getName(), e);
                throw e;
            } catch (Throwable t) {
                log.error("[ThreadPool] Callable task execute failed with Throwable", t);
                throw new RuntimeException(t);
            } finally {
                MDC.clear();
            }
        };
    }
    /** 获取线程池监控指标（可接入 Prometheus / Micrometer） */
    public ThreadPoolMetrics getMetrics() {
        return new ThreadPoolMetrics(
                poolName,
                getCorePoolSize(),
                getMaximumPoolSize(),
                getLargestPoolSize(),
                getActiveCount(),
                getPoolSize(),
                getQueue().size(),
                getQueue().remainingCapacity(),
                getCompletedTaskCount(),
                getTaskCount());
    }

    // 指标数据类
        public record ThreadPoolMetrics(String poolName, int coreSize, int maxSize, int largestPoolSize, int activeCount,
                                        int poolSize, int queueSize, int queueRemainingCapacity, long completedTaskCount,
                                        long taskCount) {
    }
}
