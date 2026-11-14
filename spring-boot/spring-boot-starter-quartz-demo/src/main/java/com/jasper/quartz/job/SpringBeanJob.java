package com.jasper.quartz.job;

import com.jasper.quartz.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 *
 * <p>为什么通常不能直接 {@code @Autowired}？
 * Quartz 默认通过 {@code JobFactory} 反射 new Job 实例，不经过 Spring，因此字段注入不会生效。
 *
 * <p>Spring Boot 的解法：
 * <ul>
 *   <li>{@code QuartzAutoConfiguration} 会创建 {@code SpringBeanJobFactory} 并设置到
 *       {@code SchedulerFactoryBean}；</li>
 *   <li>该 JobFactory 在创建 Job 时走
 *       {@code applicationContext.getAutowireCapableBeanFactory().createBean(...)}，
 *       所以构造器注入、字段注入、{@code @Value} 都能工作；</li>
 *   <li>它还会把 JobDataMap / Trigger 数据按 Bean 属性名写入 Job 对象（setter 注入）。</li>
 * </ul>
 *
 * <p>本类用构造器注入，说明“Job 不一定是无参构造”。
 * 如果你脱离 Spring 直接使用 Quartz，则 Job 必须有无参构造，且只能通过 JobDataMap 传数据。
 */
@Slf4j
public class SpringBeanJob implements Job {

    private final DemoService demoService;

    /**
     * 默认情况下，Quartz 使用自己的 JobFactory 创建实例
     * Quartz 管理 Job 的生命周期，Spring 管理业务 Bean 的生命周期。
     * 两者需要通过 JobFactory 集成，才能让 Quartz Job 使用 Spring 容器中的业务组件
     * 构造器注入：由 SpringBeanJobFactory 调用 Spring 的 createBean 完成。
     */
    public SpringBeanJob(DemoService demoService) {
        this.demoService = demoService;
    }

    @Override
    public void execute(JobExecutionContext context) {
        String result = demoService.work(context.getJobDetail().getKey().toString());
        log.info("[SpringBeanJob] 注入的 DemoService 返回：{}", result);
        context.setResult(result);
    }
}
