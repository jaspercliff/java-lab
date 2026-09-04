package jasper.basic.thread.impl;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureDemo {
    public static void main(String[] args) {
        CompletableFuture.runAsync(
                ()->{
                    System.out.println("异步任务运行中");
                }
        );
    }
}
