package com.jasper.taskExecutionAndScheduling.taskExecutor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Slf4j
public class SimpleAsyncTaskExecutorDemo {
    public static void main(String[] args) throws InterruptedException {

        try (SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("async-")) {
            executor.setTaskDecorator(new LoggingTaskDecorator());
            executor.setVirtualThreads(true);
//        表示最多允许 10 个任务并发执行,不是线程池 不会复用
            executor.setConcurrencyLimit(10);
            executor.execute(() -> {
                log.info("Thread name: {}", Thread.currentThread().getName());
                log.info("Is virtual thread: {}", Thread.currentThread().isVirtual());
            });

        }
        Thread.sleep(1000); // 等待异步任务执行完成
    }
}
