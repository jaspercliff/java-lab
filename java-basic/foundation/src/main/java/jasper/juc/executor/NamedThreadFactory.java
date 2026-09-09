package jasper.juc.executor;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jasper
 * @since 2026-09-09 <br>
 */
@Slf4j
public class NamedThreadFactory implements ThreadFactory {
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public NamedThreadFactory(String poolName) {
        this.namePrefix = "custom-pool-" + poolName + "-thread-";
    }

    @Override
    public Thread newThread(@NonNull Runnable r) {
        Thread t = new Thread(r, namePrefix + threadNumber.getAndIncrement());
        // 设置为非守护线程，确保任务执行完毕
        t.setDaemon(false);
//        线程执行的 run() 方法中抛出了异常，并且异常没有被 try-catch 捕获
//        这个处理器只能记录或处理异常，不能让已经因异常终止的线程继续执行
        t.setUncaughtExceptionHandler(
                (thread, ex) -> log.error("线程 {} 发生未捕获异常", thread.getName(), ex));
        return t;
    }
}
