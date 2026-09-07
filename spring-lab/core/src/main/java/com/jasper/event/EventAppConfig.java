package com.jasper.event;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan("com.jasper.event")
@EnableAsync // 开启异步支持
@EnableTransactionManagement
public class EventAppConfig {

    /**
     * for async
     */
    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setThreadNamePrefix("event-");
        return executor;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DemoTransactionManager();
    }
}
