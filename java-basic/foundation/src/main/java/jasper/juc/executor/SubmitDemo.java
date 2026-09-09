package jasper.juc.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author jasper
 * @since 2026-09-09 17:31:51 <br>
 *     任务不需要返回结果（纯后台异步任务，如发送通知、记录日志）。 你希望任务如果发生异常，能够立刻在控制台打印出来，或者你自定义了
 *     Thread.UncaughtExceptionHandler 来统一处理异常 <br>
 *     submit 需要获取任务的执行结果（必须用 submit 提交 Callable）。 需要控制任务的生命周期（如超时取消任务 future.get(timeout, unit)，或主动
 *     future.cancel()）
 *
 * 返回 Future
 * 可以获取任务结果
 * 可以取消任务
 * 可以判断任务是否完成
 * 任务异常会被封装到 Future 中
 * 不调用 future.get()，异常可能不会被发现
 */
public class SubmitDemo {
    public static void main(String[] args) {

        ExecutorService threadPool =
                new ThreadPoolExecutor(
                        2, // 核心线程数
                        4, // 最大线程数
                        60, // 空闲线程存活时间
                        TimeUnit.SECONDS, // 时间单位
                        new ArrayBlockingQueue<>(10), // 工作队列 默认设1000 后续压测 根据qps调整
                        new NamedThreadFactory("test"), // 线程工厂
                        // 策略会抛出一个运行时异常RejectedExecutionException
                        new ThreadPoolExecutor.CallerRunsPolicy());
        Future<?> future =
                threadPool.submit(
                        () -> {
                            System.out.println("submit 任务开始");
                            int i = 1 / 0; // 抛出 ArithmeticException
                            System.out.println("submit 任务结束"); // 不会执行
                            return 30;
                        });
        // 此时控制台没有任何异常打印！异常被吞了。

        // 必须调用 get() 才能看到异常
        try {
            Object o = future.get();
            System.out.println(o);
        } catch (ExecutionException e) {
            // 这里会捕获到 ExecutionException
            System.err.println("捕获到 submit 的异常: " + e.getCause());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadPool.shutdown();
    }
}
