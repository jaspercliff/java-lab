package com.jasper.vt;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Demo2 {
    public static void main(String[] args) {
        /*
         * 这里还是守护线程
         * ExecutorService 实现了 AutoCloseable 接口。当你退出 try 代码块时
         * 虚拟线程执行器（VirtualThreadPerTaskExecutor）会自动调用它的 .close() 方法
         * 它会调用 awaitTermination()，阻塞主线程，直到所有已提交的虚拟线程任务全部执行完毕
         */
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 10).forEach(i -> {
                executor.submit(() -> {
                    System.out.println(i);
                    Thread.sleep(Duration.ofSeconds(1));
                    return i;
                });
            });
        } // 自动等待所有任务完成
    }
}
