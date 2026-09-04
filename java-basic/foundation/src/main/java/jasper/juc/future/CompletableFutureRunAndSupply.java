package jasper.juc.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CompletableFutureRunAndSupply {
    public static void main(String[] args) {
        // 不指定线程池则使用 使用ForkJoinPool.commonPool()
        // 执行没有返回值的异步任务
        final CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> {
            System.out.println("completableFuture runAsync");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        // 执行有返回值的异步任务
        final CompletableFuture<Integer> supplyAsync = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("supplyAsync");
            return 100;
        });
        runAsync.join();
        System.out.println("supplyAsync result".concat(supplyAsync.join().toString()));
        System.out.println("main thread");
    }
}
