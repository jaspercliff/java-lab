package com.jasper.taskExecutionAndScheduling.scheduling;

import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Scheduler 线程池耗尽：导致定时任务不能及时触发。
 * <br>
 * 额外添加一个线程池，Scheduler 线程池只负责调度，任务执行交给额外线程池执行
 */
public class SingleThreadSchedulerDemo {

    public static void main(String[] args) {

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
//        ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
        ConcurrentTaskScheduler scheduler =
                new ConcurrentTaskScheduler(executor);
        // task1：每隔 2 秒触发，但每次执行耗时 10 秒
        scheduler.scheduleAtFixedRate(() -> {
            System.out.printf("[%s] task1 开始%n",
                    Thread.currentThread().getName());

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.printf("[%s] task1 结束%n",
                    Thread.currentThread().getName());

        }, Duration.ofSeconds(2));

        // task2：每隔 2 秒执行一次
        scheduler.scheduleAtFixedRate(() -> {
            System.out.printf("[%s] task2 执行%n",
                    Thread.currentThread().getName());
        }, Duration.ofSeconds(2));

    }
}
