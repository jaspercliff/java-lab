package com.jasper.vt.compare;

import com.google.common.base.Stopwatch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Traditional {
    public static void main(String[] args) {
        /*
        传统线程池：200个线程 内核就维护200个
        虚拟线程： 只有16个线程 其他都只是在用户态保存了一个变量
         */
        try (
                ExecutorService executorService = Executors.newFixedThreadPool(200);
        ) {
            Stopwatch stopwatch = Stopwatch.createStarted();
            for (int i = 0; i < 10_000; i++) {
                executorService.submit(()->{
                    try {
                        Thread.sleep(1000);//io block
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                });
            }
            executorService.shutdown(); // 手里活干完停下
            executorService.awaitTermination(1, TimeUnit.HOURS); // 等待一小时还没干完就不等了
            System.out.println(stopwatch.elapsed(TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
