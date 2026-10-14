package com.jasper.thread;

import com.jasper.constants.ThreadType;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * see {@link io.github.resilience4j.core.ExecutorServiceFactory}
 */
public final class ExecutorServiceFactory {

    private static final String SYS_PROP_KEY = "jasper.thread.type";
    private ExecutorServiceFactory() {

    }

    public static ThreadType getThreadType() {
        String s = System.getProperty(SYS_PROP_KEY);
        if (s == null) {
            System.getenv(SYS_PROP_KEY);
        }
        return ThreadType.fromStringOrDefault(s);
    }
// 指定线程名称前缀和编号起点  -vthread-0   -vthread-1
    private static ThreadFactory newVirtualThreadFactory(String poolName) {
        // JDK 21 virtual thread builder API
        return Thread.ofVirtual()
                .name(poolName + "-vthread-",  0)
                .factory();
    }

    /**
     * 当所有用户线程都结束后，JVM 可以退出，不会等待守护线程完成	eg:后台监控、辅助任务
     */
    private static ThreadFactory newPlatformThreadFactory(String poolName) {
        return Thread.ofPlatform()
                .name(poolName + "-thread-",  0)
                .daemon(true)
                .factory();
    }

    /**
     * Create a single–thread {@link ScheduledExecutorService}.
     */
    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String poolName) {
        return newSingleThreadScheduledExecutor(poolName, getThreadType());
    }
    /**
     * Create a single–thread {@link ScheduledExecutorService} with the specified thread type.
     */
    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String poolName, ThreadType threadType) {
        if (threadType == ThreadType.VIRTUAL) {
            /*
             * Create a single-thread scheduled executor whose worker thread is created by
             * the virtual-thread {@link ThreadFactory} returned from
             * {@link #newVirtualThreadFactory(String)}.
             */
            return Executors.newSingleThreadScheduledExecutor(newVirtualThreadFactory(poolName));
        } else {
            return Executors.newSingleThreadScheduledExecutor(newPlatformThreadFactory(poolName));
        }
    }

    /**
     * Create a {@link ScheduledExecutorService} with the given pool size.
     */
    public static ScheduledExecutorService newScheduledThreadPool(int size, String poolName) {
        return newScheduledThreadPool(size, poolName, getThreadType());
    }

    /**
     * Create a {@link ScheduledExecutorService} with the given pool size and thread type.
     * core pool size
     */
    public static ScheduledExecutorService newScheduledThreadPool(int size, String poolName, ThreadType threadType) {
        if (threadType == ThreadType.VIRTUAL) {
            return Executors.newScheduledThreadPool(size, newVirtualThreadFactory(poolName));
        } else {
            return Executors.newScheduledThreadPool(size, newPlatformThreadFactory(poolName));
        }
    }



}
