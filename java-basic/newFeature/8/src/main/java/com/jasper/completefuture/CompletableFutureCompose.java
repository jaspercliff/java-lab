package com.jasper.completefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class CompletableFutureCompose {
    public static void main(String[] args) {
        System.out.println("===================串行操作 start");
        final CompletableFuture<Integer> supplyAsync = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync");
            return 10;
        });
        // 对结果应用一个function
        final CompletableFuture<Integer> integerCompletableFuture = supplyAsync.thenApply(i -> i * 2);
        final CompletableFuture<CompletableFuture<Integer>> com = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync1");
            return 10;
        }).thenApply(res -> CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync2");
            return res * 2;
        }));
        // 得join俩遍才能得到想要的结果
        System.out.println(com.join().join());

        // 扁平化(将嵌套的结构转换为更浅更直接的结构)在当前com完成之后返回一个新的completableFuture
        final CompletableFuture<Integer> com1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync1");
            return 20;
        }).thenCompose(res -> CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync2");
            return res * 2;
        }));
        System.out.println(com1.join());
        System.out.println("===================串行操作 end");

        System.out.println("===================消费结果 start");
        // 对结果应用一个consumer   thenApply的主要目的是转换 thenAccept的主要目的是消费
        integerCompletableFuture.thenAccept(System.out::println);
        System.out.println("===================消费结果 end");


        System.out.println("===================并行组合 start");
        final CompletableFuture<Integer> a = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync1");
            return 20;
        });
        final CompletableFuture<Integer> b = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync2");
            return 10;
        });
        // 等待俩个都完成 然后对他们的结果进行操作 biFunction
        final CompletableFuture<Integer> c = a.thenCombine(b, Integer::sum);
        System.out.println(c.join());
        System.out.println("===================并行组合 end");
        System.out.println("===================选择执行 end");
        final CompletableFuture<String> d = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("supplyAsync1");
            return "fast";
        });
        final CompletableFuture<String> e = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e1) {
                throw new RuntimeException(e1);
            }
            System.out.println("supplyAsync1");
            return "slow";
        });
        // 那个先完成 就用那个的结果转换
        final CompletableFuture<String> win = d.applyToEither(e, s -> s.concat("win"));
        //消费先完成的任务的结果
        d.acceptEither(e,s->System.out.println("accept either ".concat(s.concat(" win"))));
        System.out.println("win.join() = " + win.join());
        // 只要有任意一个完成就执行后续操作
        d.runAfterEither(e,()-> System.out.println("runAfterEither"));
        System.out.println("===================选择执行 end");


        System.out.println("===================等待所有或者任意 start");
        final CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "A");
        final CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "B");
        final CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> "C");

        final CompletableFuture<Void> all = CompletableFuture.allOf(f1, f2, f3);
        all.thenRun(()-> System.out.println(Stream.of(f1,f2,f3).map(CompletableFuture::join).toList()));

        final CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            return "first";
        });

        final CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            return "last";
        });

        final CompletableFuture<Object> any = CompletableFuture.anyOf(cf1, cf2);;
        any.thenAccept(System.out::println);
        any.join();
        System.out.println("===================等待所有或者任意 start");
    }
}
