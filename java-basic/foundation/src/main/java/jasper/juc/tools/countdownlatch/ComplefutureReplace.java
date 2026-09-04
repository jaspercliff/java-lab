package jasper.juc.tools.countdownlatch;

import java.util.concurrent.CompletableFuture;

/**
 * @author jasper
 * @since 2026-05-25 12:43:27 <br>
 *     没有返回值 没有链式任务 还是countdownlatch 简单明了 <br>
 *     comple 更适合io并发，因为取的是等待时间最大的一个 而不是串行加起来 <br>
 *     50 + 30 + 40 + 60 + 80 = 260m 但是compl 只需要80m 查询商品详情页
 */
public class ComplefutureReplace {
    public static void main(String[] args) {
        CompletableFuture<Void> f1 =
                CompletableFuture.runAsync(
                        () -> {
                            System.out.println("thread: 1 is done");
                        });
        CompletableFuture<Void> f2 =
                CompletableFuture.runAsync(
                        () -> {
                            System.out.println("thread: 2 is done");
                        });
        CompletableFuture<Void> f3 =
                CompletableFuture.runAsync(
                        () -> {
                            System.out.println("thread: 3 is done");
                        });
        CompletableFuture.allOf(f1, f2, f3).join();
        System.out.println("all task finished");
    }
}
