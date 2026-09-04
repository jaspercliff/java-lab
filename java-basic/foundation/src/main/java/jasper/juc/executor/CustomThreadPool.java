package jasper.juc.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 核心线程数  </br>
 * cpu密集型：n(系统的核心:应用被分配的cpu资源配额)+1 </br>
 * n个核心 n个线程 每个线程独占一个核心 可以达到100%的cpu利用率，但是会有一些不可避免的阻塞（线程调度等微小的开销） +1提供了一个短暂缓冲
 *
 *
 * IO密集型：n/（1-BlockCoefficient)
 * 阻塞系数：一个线程执行任务的完整周期，其中io等待时间占总时间的比例
 */
public class CustomThreadPool {
    public static void main(String[] args) {

        final ThreadPoolExecutor customThreadPool = new ThreadPoolExecutor(
                5, //核心线程数
                10,//最大线程数
                10,//非空闲线程等待时间
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),// 工作队列
                // Executors.defaultThreadFactory(),//线程创建工厂
                new SimpleThreadFactory("custom thread pool"), //自定义线程创建工厂
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
