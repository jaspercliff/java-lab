package com.jasper.completefuture;

import java.util.concurrent.*;

public class CompletableFutureComplete {
    public static void main(String[] args) throws InterruptedException {
        final CompletableFuture<String> future = new CompletableFuture<>();
        final CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                // true 表示是手动完成的  false表示原本就已经完成了 这里complete了不会影响原任务的继续执行，但是后续结果不会影响当前任务了
                final boolean complete = future.complete("手动完成");
                if (complete) {
                    System.out.println("time out manual complete");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        voidCompletableFuture.join();

        final CompletableFuture<String> future1 = new CompletableFuture<>();
        CompletableFuture.runAsync(()->{
            try {
                TimeUnit.SECONDS.sleep(2);
                future1.completeExceptionally(new TimeoutException());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        // 如果该future 未完成，则将其设置为异常完成 并且后续的 thenApply accept等不会执行  但是exceptionally会执行
        final CompletableFuture<String> exceptionally = future1.thenApply(res -> {
                    System.out.println("this will not print! ");
                    return res;
                })
                .exceptionally(e -> {
                    System.out.println("exceptionally ");
                    return "exception default";
                });

        System.out.println(exceptionally.join());

        final ExecutorService executorService = Executors.newSingleThreadExecutor();

        final CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
                try {
                    TimeUnit.SECONDS.sleep(1); //确保有时间被中断
                } catch (InterruptedException e) {
                    System.out.println("thread is interrupted");
                    Thread.currentThread().interrupt(); //重新设置中断标志
                    return;
                }
            }
            System.out.println("任务正常完成");
        }, executorService);

        TimeUnit.SECONDS.sleep(1);
        // true 允许打断正在运行中的任务
        // false 只取消没有开始的任务
        // cancel() 不会中断正在执行的 supplyAsync 或 runAsync 中的代码 只是为了实现future 接口保留
        final boolean cancel = future2.cancel(true);
        System.out.println("cancel = " + cancel);
        executorService.shutdown();
    }
}
