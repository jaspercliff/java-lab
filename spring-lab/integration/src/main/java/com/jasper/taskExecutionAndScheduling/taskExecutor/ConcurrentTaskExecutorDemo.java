package com.jasper.taskExecutionAndScheduling.taskExecutor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


@Slf4j
public class ConcurrentTaskExecutorDemo {
    public static void main(String[] args) {
        Executor executor = Executors.newFixedThreadPool(10);
//        把 JDK 的 Executor 适配成 Spring 的 TaskExecutor
        TaskExecutor taskExecutor = new ConcurrentTaskExecutor(executor);
        taskExecutor.execute(() ->
                log.info("Thread name: {}", Thread.currentThread().getName())
        );
    }
}
