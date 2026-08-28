package com.jasper;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

/** System.currentTimeMillis depends on system time */
public class stopWatchDemo {
    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        System.out.println("stopwatch = " + stopwatch);
        for (int i = 0; i < 10000; i++) {
            Math.random();
        }
        System.out.println("stopwatch.isRunning() = " + stopwatch.isRunning());
        stopwatch.stop();
        System.out.println("after stop stopwatch.stop() = " + stopwatch.isRunning());
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("elapsed = " + elapsed + "ms");

        System.out.println("elapsed = " + elapsed);

        System.out.println("stopwatch = " + stopwatch);
        // reset之后才会清0 不然重新使用会继续累加
        Stopwatch reset = stopwatch.reset();
        System.out.println("reset = " + reset);
    }
}
