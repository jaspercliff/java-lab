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
                        // 任务耗时短、突发性强，希望立刻执行？ 不保存任务，生产者线程必须等待消费者线程来接手任务
                        // SynchronousQueue。（配合较大的 maximumPoolSize，类似 CachedThreadPool 的行为，但必须手动控制最大线程数）。

                        //任务耗时较长、处理速度不均匀，需要缓冲削峰？
                        //选 LinkedBlockingQueue（指定合理容量，如 1000~5000）。这是最稳妥、最常用的选择。

                        //核心交易链路，对 GC 敏感，追求极致且稳定的吞吐？
                        // 内存预分配，创建时直接分配好数组空间，运行期间没有节点创建和回收的开销，对 GC 非常友好。
                        //因为只有一把锁，在极高并发下，锁竞争会比 LBQ 激烈，吞吐量上限略低于 LBQ
                        //选 ArrayBlockingQueue（指定容量，如 500~1000）
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
