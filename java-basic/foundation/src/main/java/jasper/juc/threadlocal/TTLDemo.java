package jasper.juc.threadlocal;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jasper
 * @since 2026-05-25 <br>
 */
public class TTLDemo {

    private static final TransmittableThreadLocal<String> context =
            new TransmittableThreadLocal<>();

    private static final ExecutorService executor =
            TtlExecutors.getTtlExecutorService(Executors.newFixedThreadPool(1));

    public static void main(String[] args) throws Exception {

        context.set("任务A");

        executor.submit(
                () -> {
                    System.out.println("任务1: " + context.get());
                });

        Thread.sleep(1000);

        context.set("任务B");

        executor.submit(
                () -> {
                    System.out.println("任务2: " + context.get());
                });

        executor.shutdown();
    }
}
