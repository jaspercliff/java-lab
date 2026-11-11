package com.jasper.trace.config;

import com.jasper.trace.context.TraceIdProvider;
import com.jasper.trace.filter.TraceIdFilter;
import com.jasper.trace.task.MdcTaskDecorator;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.EnumSet;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "jasper.trace",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true // 属性不存在时也匹配 默认为true
)
@EnableConfigurationProperties(TraceProperties.class)
public class TraceAutoConfiguration {

    @Bean
    //只有当 Spring 容器中不存在指定类型的 Bean 时，才创建当前 Bean。
    //给用户提供默认实现，同时允许用户自定义覆盖
    @ConditionalOnMissingBean
    public TraceIdProvider traceIdProvider(
            TraceProperties properties) {

        return new TraceIdProvider(properties.mdcKey());
    }

    @Bean
    @ConditionalOnMissingBean
    public TraceIdFilter traceIdFilter(
            TraceProperties properties,
            TraceIdProvider traceIdProvider) {

        return new TraceIdFilter(properties, traceIdProvider);
    }

    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceFilterRegistration(
            TraceIdFilter filter,
            TraceProperties properties) {

        FilterRegistrationBean<TraceIdFilter> registration =
                new FilterRegistrationBean<>();

        registration.setFilter(filter);
        registration.setName("traceIdFilter");
        registration.addUrlPatterns("/*");

        registration.setOrder(Integer.MIN_VALUE + 100);

        registration.setDispatcherTypes(
                EnumSet.of(
                        DispatcherType.REQUEST,
                        DispatcherType.ASYNC
                )
        );
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "jasper.trace.async",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public MdcTaskDecorator mdcTaskDecorator() {
        return new MdcTaskDecorator();
    }

    @Bean(name = "traceTaskExecutor")
    @ConditionalOnMissingBean(name = "traceTaskExecutor")
    @ConditionalOnProperty(
            prefix = "jasper.trace.async",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public Executor traceTaskExecutor(
            TraceProperties properties,
            MdcTaskDecorator decorator) {

        TraceProperties.Async async = properties.async();

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(async.corePoolSize());
        executor.setMaxPoolSize(async.maxPoolSize());
        executor.setQueueCapacity(async.queueCapacity());

        executor.setKeepAliveSeconds(async.keepAliveSeconds());

        executor.setThreadNamePrefix(async.threadNamePrefix());
        // 关键：接入 MDC 上下文传播 只对配置了这个 Decorator 的线程池生效，不会自动覆盖项目中的所有线程池
        // completableFuture默认使用 ForkJoinPool.commonPool() 不会生效
        executor.setTaskDecorator(decorator);

        // 队列满且线程池达到最大容量时，拒绝任务 异步线程池达到上限时，直接拒绝并让调用方明确感知
        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.AbortPolicy()
        );

        executor.setWaitForTasksToCompleteOnShutdown(
                async.waitForTasksToCompleteOnShutdown()
        );

        executor.setAwaitTerminationSeconds(
                async.awaitTerminationSeconds()
        );

        return executor;
    }
}
