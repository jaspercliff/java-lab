package com.jasper.taskExecutionAndScheduling.taskExecutor;

import org.springframework.core.task.TaskExecutor;

public class TaskExecutorExample {

    public static void main(String[] args) {
        TaskExecutor taskExecutor = Runnable::run; // 使用同步执行器，直接在当前线程执行任务
        for (int i = 0; i < 5; i++) {
            taskExecutor.execute(() -> System.out.println("Message" ));        }
    }
}
