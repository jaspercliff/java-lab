package jasper.juc.executor.demo;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author jasper
 * @since 2026-09-09 <br>
 */
@Slf4j
public class AlertRejectedExecutionHandler implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        // 1. 记录拒绝日志，触发监控告警
        log.error(
                "线程池 [{}] 已满，触发拒绝策略！当前活跃线程: {}, 队列大小: {}",
                ((ProductionThreadPoolExecutor) executor).getPoolName(),
                executor.getActiveCount(),
                executor.getQueue().size());

        // 2. 降级策略：根据业务重要性选择
        // 策略A：抛出异常，让上层捕获处理（适合核心交易链路）
        throw new RejectedExecutionException(
                "Task " + r.toString() + " rejected from " + executor.toString());

        // 策略B：调用者运行策略（适合非核心、允许延迟的批量任务）
        // r.run();

        // 策略C：持久化到 MQ/Redis/DB，后续定时任务重试（适合绝对不能丢失的任务）
        // saveToDbForRetry(r);
    }
}
