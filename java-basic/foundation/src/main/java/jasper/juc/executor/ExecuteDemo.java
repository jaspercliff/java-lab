package jasper.juc.executor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 没有返回值
 * 方法参数是 Runnable
 * 任务异常会直接交给线程的 UncaughtExceptionHandler
 * 异常通常会打印堆栈，并可能导致工作线程退出
 */
@Slf4j
@SuppressWarnings({"NumericOverflow", "divzero"})
public class ExecuteDemo {
    public static void main(String[] args) {

        try (ExecutorService threadPool =
                new ThreadPoolExecutor(
                        2, // 核心线程数
                        4, // 最大线程数
                        60, // 空闲线程存活时间
                        TimeUnit.SECONDS, // 时间单位
                        new ArrayBlockingQueue<>(10), // 工作队列 默认设1000 后续压测 根据qps调整
                        new NamedThreadFactory("test"), // 线程工厂
                        // 策略会抛出一个运行时异常RejectedExecutionException
                        new ThreadPoolExecutor.CallerRunsPolicy())){
            threadPool.execute(
                    () -> {
                        log.info("start");
                        int i = 1 / 0; // 抛出 ArithmeticException
                        log.info("end");
                    });
            // 控制台会直接打印 Exception 堆栈信息，且线程池会新建一个线程。
        }
    }
}
