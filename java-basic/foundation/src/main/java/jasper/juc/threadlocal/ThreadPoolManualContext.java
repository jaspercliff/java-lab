package jasper.juc.threadlocal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jasper
 * @since 2026-05-25 <br>
 */
public class ThreadPoolManualContext {

    private static final ThreadLocal<String> context = new ThreadLocal<>();

    private static final ExecutorService executor = Executors.newFixedThreadPool(1);

    public static void main(String[] args) throws InterruptedException {

        // ================= 任务A =================
        // 就相当于是先设置到父线程 然后在手动复制到子线程
        context.set("任务A的标识");

        submitTask(
                () -> {
                    System.out.println("任务1获取值: " + context.get());
                });

        Thread.sleep(1000);
        // ================= 任务B =================
        context.set("任务B的标识");
        submitTask(
                () -> {
                    System.out.println("任务2获取值: " + context.get());
                });
        executor.shutdown();
    }

    /** 手动传递 ThreadLocal */
    private static void submitTask(Runnable task) {
        // 捕获父线程上下文
        String parentValue = context.get();
        executor.submit(
                () -> {
                    try {
                        //  设置到子线程
                        context.set(parentValue);
                        // 执行任务
                        task.run();
                    } finally {
                        //  清理（非常重要）
                        context.remove();
                    }
                });
    }
}
