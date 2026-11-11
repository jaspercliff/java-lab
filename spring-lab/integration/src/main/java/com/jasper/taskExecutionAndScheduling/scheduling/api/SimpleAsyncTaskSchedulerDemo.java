package com.jasper.taskExecutionAndScheduling.scheduling.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

import java.time.Duration;

@Slf4j
public class SimpleAsyncTaskSchedulerDemo {

    public static void main(String[] args) {
//        一个调度线程负责安排任务，每次触发任务时，都会创建一个新的执行线程，而不是使用固定大小的线程池
        try(SimpleAsyncTaskScheduler scheduler = new SimpleAsyncTaskScheduler();){

            // 开启虚拟线程
            scheduler.setVirtualThreads(true);

            // 设置线程名称前缀
            scheduler.setThreadNamePrefix("virtual-task-");

            // 限制最大并发执行数量
            scheduler.setConcurrencyLimit(10);

            scheduler.start();

            // 1. 固定频率，每隔 2 秒执行
            scheduler.scheduleAtFixedRate(
                    () -> {
                        log.info("fixedRate: {}, virtual={}",
                                Thread.currentThread(),
                                Thread.currentThread().isVirtual());

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    },
                    Duration.ofSeconds(2)
            );
        }
    }
}