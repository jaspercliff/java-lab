package com.jasper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AsyncConfig {
    @Bean
    public TaskExecutor simpleAsyncTaskExecutor() {
        SimpleAsyncTaskExecutor executor =
                new SimpleAsyncTaskExecutor("async-");
        executor.setVirtualThreads(true);
        return executor;
    }

    /**
     * 封装了 JDK 的 ThreadPoolExecutor，并提供 Spring 风格的 Bean 配置
     */
    @Bean("applicationTaskExecutor")
    public ThreadPoolTaskExecutor applicationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(5);
        // 最大线程数
        executor.setMaxPoolSize(10);
        // 阻塞队列容量
        executor.setQueueCapacity(100);
        // 空闲线程存活时间
        executor.setKeepAliveSeconds(60);
        // 线程名称前缀
        executor.setThreadNamePrefix("app-async-");
        // 拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 优雅关闭
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);

        return executor;
    }

}
