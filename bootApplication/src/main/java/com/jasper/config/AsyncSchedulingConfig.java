package com.jasper.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * async method
 * new thread（）<br>
 * @Async  <br>
 * TaskExecutor SimpleAsyncTaskExecutor  ThreadPoolTaskExecutor
 */
@Configuration
@EnableScheduling
@EnableAsync
@Slf4j
public class AsyncSchedulingConfig implements AsyncConfigurer {

    /**
     * 专门负责定时调度
     */
    @Bean("taskScheduler")
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("scheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);

        return scheduler;
    }
    @Bean
    public TaskExecutor simpleAsyncTaskExecutor() {
        SimpleAsyncTaskExecutor executor =
                new SimpleAsyncTaskExecutor("async-");
        executor.setVirtualThreads(true);
        return executor;
    }
    /**
     * 封装了 JDK 的 ThreadPoolExecutor，并提供 Spring 风格的 Bean 配置
     * 专门负责业务任务执行
     */
    @Bean("taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("task-");

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);

        // 默认采用 AbortPolicy，拒绝时抛出异常
        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.AbortPolicy()
        );

        return executor;
    }
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {

        return (ex, method, params) -> {
            log.error("异步任务执行失败: {}", method.getName(), ex);
        };
    }
}