package com.jasper.monitor;

import com.jasper.functional.CheckedRunnable;
import com.jasper.functional.CheckedSupplier;
import lombok.extern.slf4j.Slf4j;


import java.util.concurrent.TimeUnit;

/**
 * @author jasper
 * @since 2026-07-15 23:22:47 <br>
 */
@Slf4j
public class TimeTracker implements AutoCloseable {
    private static final ThreadLocal<Integer> LEVEL = ThreadLocal.withInitial(() -> 0);

    private final String name;
    private final long start;
    private final boolean silent;
    private boolean success = true;

    private static String indent(int level) {
        return "   ".repeat(level);
    }

    public TimeTracker(String name) {
        this(name, false);
    }

    private TimeTracker(String name, boolean silent) {
        this.name = name;
        this.start = System.nanoTime();
        this.silent = silent;
        if (!silent) {
            int level = LEVEL.get();
            // log.atInfo().log(() -> indent(level) + ">" + name);
            log.info("{} {} start", indent(level), name);
            LEVEL.set(level + 1);
        }
    }

    public static TimeTracker start(String name) {
        return new TimeTracker(name);
    }

    /**
     * 静默计时：不打日志、不改动嵌套层级，只提供基于 nanoTime 的耗时。
     *
     * <p>适合访问日志这类需要自行组织日志级别（慢请求 WARN）与字段（traceId、状态码）的场景，
     * 避免 start/end 两条日志把一次请求拆成两条记录。
     */
    public static TimeTracker silent(String name) {
        return new TimeTracker(name, true);
    }

    /** 已耗时（毫秒），基于 nanoTime，不受系统时钟调整影响。 */
    public long elapsedMillis() {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    }

    public void fail() {
        success = false;
    }

    @Override
    public void close() {
        if (silent) {
            return;
        }
        int currentLevel = LEVEL.get();
        int parentLevel = Math.max(0, currentLevel - 1);

        try {
            long elapsed = elapsedMillis();
            log.info(
                    "{} name:{}  elapsed:{} ms, success:{}",
                    indent(parentLevel),
                    name,
                    elapsed,
                    success);
        } finally {
            // 无论上面打印日志时发生什么意外，当前层的状态必须清除，防止污染下一个请求
            if (parentLevel == 0) {
                LEVEL.remove(); // 彻底释放 ThreadLocal，杜绝线程池引发的内存泄漏
            } else {
                LEVEL.set(parentLevel); // 降级回父层级
            }
        }
    }

    /** 欺骗编译器，直接抛出受检异常而不需要在方法签名上声明 throws */
    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T sneakyThrow(Throwable t) throws T {
        throw (T) t; // 利用泛型擦除，在编译后这里等同于 throw t;
    }

    public static void measure(String name, CheckedRunnable runnable) {
        try (TimeTracker tracker = TimeTracker.start(name)) {
            try {
                runnable.run();
            } catch (Throwable e) {
                tracker.fail();
                throw e; // 原封不动抛出，交给外层擦除
            }
        } catch (Throwable e) {
            //  只要出任何错，直接偷偷抛出，保持原始堆栈，不用肉眼可见地写包装！
            throw sneakyThrow(e);
        }
    }

    public static <T> T measure(String name, CheckedSupplier<T> supplier) {
        try (TimeTracker tracker = TimeTracker.start(name)) {
            try {
                return supplier.get();
            } catch (Throwable e) {
                tracker.fail();
                throw e; // 原封不动抛出，交给外层擦除
            }
        } catch (Throwable e) {
            throw sneakyThrow(e);
        }
    }
}
