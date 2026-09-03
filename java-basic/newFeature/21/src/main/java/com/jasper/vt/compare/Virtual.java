package com.jasper.vt.compare;

import com.google.common.base.Stopwatch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Virtual {
    public static void main(String[] args) {

        Stopwatch stopwatch = Stopwatch.createStarted();
        try(
                ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
                ){
            for (int i = 0; i < 10_000; i++) {
                executor.submit(()->{
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                });
            }
        }
        // 要在try之后
        System.out.println(stopwatch.elapsed(TimeUnit.SECONDS));
    }
}
