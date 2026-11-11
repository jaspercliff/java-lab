package com.jasper.taskExecutionAndScheduling.taskExecutor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class ThreadPoolTaskExecutorDemo {
    public static void main(String[] args) {
        // 对 JDK 的 ThreadPoolExecutor 的封装
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("app-async-");
        executor.setTaskDecorator(new LoggingTaskDecorator());
        executor.initialize();
        executor.execute(() -> {
            System.out.println("Thread name: " + Thread.currentThread().getName());
        });
    }
}
