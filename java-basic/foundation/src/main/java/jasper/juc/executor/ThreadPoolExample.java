package jasper.juc.executor;

import java.util.concurrent.*;

/**
 * 核心线程数 <br>
 * cpu密集型：n(系统的核心:应用被分配的cpu资源配额)+1 <br>
 * n个核心 n个线程 每个线程独占一个核心 可以达到100%的cpu利用率，但是会有一些不可避免的阻塞（线程调度等微小的开销） +1提供了一个短暂缓冲
 *
 * <p>IO密集型：n/（1-BlockCoefficient) 阻塞系数：一个线程执行任务的完整周期，其中io等待时间占总时间的比例
 *
 * @author jasper
 * @since 2026-09-09 <br>
 */
public class ThreadPoolExample {
    public static void main(String[] args) {
        // 创建线程池
        ExecutorService threadPool =
                new ThreadPoolExecutor(
                        2, // 核心线程数
                        4, // 最大线程数
                        60, // 空闲线程存活时间
                        TimeUnit.SECONDS, // 时间单位
                        new ArrayBlockingQueue<>(10), // 工作队列 默认设1000 后续压测 根据qps调整
                        Executors.defaultThreadFactory(), // 线程工厂
                        //                         new ThreadPoolExecutor.AbortPolicy()
                        // 策略会抛出一个运行时异常RejectedExecutionException
                        new ThreadPoolExecutor.CallerRunsPolicy()
                        // 推荐：由提交任务的线程（如 main 线程）自己去执行这个任务
                        // new ThreadPoolExecutor.DiscardPolicy()
                        // 直接丢弃新任务，不抛异常（慎用，数据会丢）
                        // new ThreadPoolExecutor.DiscardOldestPolicy()
                        //  丢弃队列中最老的一个任务，把新任务加进去
                        );
        // 提交任务
        for (int i = 0; i < 20; i++) {
            final int taskID = i;
            threadPool.execute(
                    () -> {
                        System.out.println(
                                "执行任务：" + taskID + "，线程名：" + Thread.currentThread().getName());
                        try {
                            TimeUnit.SECONDS.sleep(2); // 模拟任务执行时间
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
        }

        threadPool.shutdown(); // 关闭线程池
    }
}
