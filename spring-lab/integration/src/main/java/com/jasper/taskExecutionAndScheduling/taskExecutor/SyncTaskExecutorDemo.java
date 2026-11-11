package com.jasper.taskExecutionAndScheduling.taskExecutor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

/**
 * 原本使用线程池执行的任务，可以临时替换为同步执行器，方便调试调用链
 */
@Slf4j
public class SyncTaskExecutorDemo {
    public static void main(String[] args) {
        //不创建新线程，直接在当前线程执行任务
        TaskExecutor executor = new SyncTaskExecutor();
        executor.execute(() ->
                log.info("Thread name: {}", Thread.currentThread().getName())
                );
    }
}
