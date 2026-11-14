package com.jasper.quartz.config;

import lombok.extern.slf4j.Slf4j;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * 自定义 JobFactory：保留 Spring 的依赖注入能力，同时打印 Job 实例创建日志。
 *
 * <p>继承 {@link SpringBeanJobFactory} 的原因：
 * <ul>
 *   <li>它实现了 {@code ApplicationContextAware}，内部通过
 *       {@code AutowireCapableBeanFactory#createBean} 创建 Job，因此支持构造器/字段注入；</li>
 *   <li>它还会把 JobDataMap、Trigger 数据按属性名注入 Job 的 setter。</li>
 * </ul>
 *
 * <p>如果直接实现 {@code JobFactory} 并 {@code newInstance()}，就会丢掉 Spring 注入能力，
 * 这是接入 Quartz 时最常见的坑之一。
 *
 * <p>本类通过 {@code SchedulerFactoryBeanCustomizer} 替换掉 Spring Boot 默认创建的
 * {@code SpringBeanJobFactory}；替换后必须手动 {@code setApplicationContext}，否则注入失效。
 */
@Slf4j
public class TracingJobFactory extends SpringBeanJobFactory {

    public TracingJobFactory(ApplicationContext applicationContext) {
        // 关键：把 ApplicationContext 交给 SpringBeanJobFactory，后续创建 Job 时才能注入 Bean
        setApplicationContext(applicationContext);
    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object job = super.createJobInstance(bundle);
        if (log.isDebugEnabled()) {
            log.debug("创建 Job 实例：class={} jobKey={}",
                    job.getClass().getName(), bundle.getJobDetail().getKey());
        }
        return job;
    }
}
