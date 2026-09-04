package jasper.juc.future;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureDemo {
    public static void main(String[] args) {
        CompletableFuture<Integer> completeFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync");
            return 42;
        });
        // apply 对其结果应用一个Function  accept 对结果应用一个Consumer 属于回调 和get阻塞不一样
        completeFuture.thenApply(i -> i*2).thenAccept(result -> System.out.println("异步处理的结果".concat(result.toString())));
        completeFuture.thenAccept(System.out::println);

        CompletableFuture<Integer> a = CompletableFuture.supplyAsync(() -> 10);
        CompletableFuture<Integer> b = CompletableFuture.supplyAsync(() -> 16);
        //组合   join也是阻塞的 join 抛出CompletionException
        CompletableFuture<Integer> combine = a.thenCombine(b, Integer::sum);
        System.out.println("combine = " + combine.join());

        CompletableFuture<Integer> exceptionally = CompletableFuture.supplyAsync(() -> {
            if (true) throw new RuntimeException();
            return 15;
        }).exceptionally(throwable -> {
            System.out.println("throwable = " + throwable);
            return 0;
        });
        System.out.println("exceptionally = " + exceptionally.join());
    }
}
