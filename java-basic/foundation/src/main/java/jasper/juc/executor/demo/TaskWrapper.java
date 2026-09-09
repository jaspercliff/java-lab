package jasper.juc.executor.demo;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;

import java.util.Map;

/**
 * @author jasper
 * @since 2026-09-09 <br>
 *     Spring 的 @Async 或原生线程池默认不会传递 MDC，且 execute 方法如果抛出异常，默认只会打印到 stderr，不会进入业务日志系统
 */
@Slf4j
public class TaskWrapper implements Runnable {
    private final Runnable delegate;
    // 捕获主线程的 MDC 上下文
    private final Map<String, String> contextMap;

    public TaskWrapper(Runnable delegate) {
        this.delegate = delegate;
        this.contextMap = MDC.getCopyOfContextMap();
    }

    @Override
    public void run() {
        // 1. 恢复上下文
        if (contextMap != null) {
            MDC.setContextMap(contextMap);
        }
        try {
            // 2. 执行真实任务
            delegate.run();
        } catch (Exception e) {
            // 3. 异常兜底：防止异常被吞没，记录到日志系统
            log.error("线程池任务执行异常, threadName: {}", Thread.currentThread().getName(), e);
        } finally {
            // 4. 清理上下文，防止线程复用时 MDC 污染
            MDC.clear();
        }
    }
}
