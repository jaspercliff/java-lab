package jasper.juc.executor.demo;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author jasper
 * @since 2026-09-09 <br>
 */
@Slf4j
// @Configuration
public class ThreadPoolConfig {

    private ProductionThreadPoolExecutor orderThreadPool;

    // @Bean("orderThreadPool")
    public ProductionThreadPoolExecutor orderThreadPool() {
        // 参数配置建议：
        // 核心线程数：CPU核数 * 2 (IO密集型) 或 CPU核数 + 1 (CPU密集型)
        // 队列类型：强烈建议使用有界队列 LinkedBlockingQueue，防止 OOM
        this.orderThreadPool =
                new ProductionThreadPoolExecutor(
                        "order-pool",
                        10, // corePoolSize
                        50, // maximumPoolSize
                        60L,
                        TimeUnit.SECONDS, // keepAliveTime
                        new LinkedBlockingQueue<>(1000), // 有界队列，容量1000
                        new AlertRejectedExecutionHandler() // 自定义拒绝策略
                        );
        // 允许核心线程超时回收（节省资源）
        this.orderThreadPool.allowCoreThreadTimeOut(true);
        return this.orderThreadPool;
    }

    /** 优雅停机：Spring 容器关闭时调用 */
    // @PreDestroy
    public void shutdownGracefully() {
        if (orderThreadPool == null) return;

        log.info("开始优雅关闭线程池: {}", orderThreadPool.getPoolName());
        orderThreadPool.shutdown(); // 停止接收新任务

        try {
            // 等待已有任务执行完毕，最多等待 60 秒
            if (!orderThreadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                log.warn("线程池未在 60 秒内关闭，强制 shutdownNow");
                orderThreadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("线程池关闭被中断", e);
            orderThreadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("线程池 {} 已完全关闭", orderThreadPool.getPoolName());
    }
}
