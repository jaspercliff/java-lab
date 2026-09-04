package jasper.juc.threadlocal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jasper
 * @since 2026-05-25 <br>
 */
public class ThreadPoolITLFailure {
    // 使用 InheritableThreadLocal
    private static ThreadLocal<String> context = new InheritableThreadLocal<>();

    // 线程池中的线程是预先创建好的，不会在每次提交任务时都触发“父传子”的拷贝
    // 创建一个只有一个核心线程的线程池，方便复现问题
    private static ExecutorService executor = Executors.newFixedThreadPool(1);

    public static void main(String[] args) throws InterruptedException {
        // --- 第一个任务 ---
        context.set("任务A的标识");
        executor.submit(
                () -> {
                    // 此时线程池创建了新线程，拷贝成功
                    System.out.println("线程池任务1获取值: " + context.get());
                });

        Thread.sleep(1000); // 确保第一个任务执行完，线程回到池子里闲置

        // ---  修改父线程的值 ---
        context.set("任务B的标识");
        System.out.println("主线程修改值为: " + context.get());

        // ---  第二个任务 ---
        executor.submit(
                () -> {
                    // 关键点：由于线程池复用了之前的线程，它不会再次执行拷贝动作
                    // 它拿到的依然是第一次拷贝进来的“任务A的标识”，或者更糟，拿到的是上一个任务遗留的值
                    System.out.println("线程池任务2获取值: " + context.get());
                });

        executor.shutdown();
    }
}
