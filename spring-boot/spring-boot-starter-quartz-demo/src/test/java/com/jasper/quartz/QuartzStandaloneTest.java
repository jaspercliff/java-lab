package com.jasper.quartz;

import org.junit.jupiter.api.Test;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 不依赖 Spring 的 Quartz 最小可运行示例。
 *
 * <p>这段代码也说明了 Quartz 的本质：
 * <ol>
 *   <li>用 Properties 配置 SchedulerFactory（等价于 quartz.properties）；</li>
 *   <li>创建 JobDetail（做什么）和 Trigger（什么时候做）；</li>
 *   <li>{@code scheduleJob} 注册，{@code start} 启动；</li>
 *   <li>用完必须 {@code shutdown}，否则线程池会让 JVM 不退出。</li>
 * </ol>
 *
 * <p>测试用 CountDownLatch 等待异步执行，避免 {@code Thread.sleep} 带来的不稳定。
 */
class QuartzStandaloneTest {

    @Test
    void shouldExecuteJobScheduledWithRamJobStore() throws Exception {
        LatchJob.reset();

        Properties props = new Properties();
        props.setProperty("org.quartz.scheduler.instanceName", "standalone-test");
        props.setProperty("org.quartz.scheduler.instanceId", "test");
        props.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        props.setProperty("org.quartz.threadPool.threadCount", "2");
        props.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        props.setProperty("org.quartz.scheduler.skipUpdateCheck", "true");

        Scheduler scheduler = new StdSchedulerFactory(props).getScheduler();
        try {
            JobDetail job = JobBuilder.newJob(LatchJob.class)
                    .withIdentity("latchJob", "test")
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("latchTrigger", "test")
                    .forJob(job)
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(1)
                            .withRepeatCount(0))
                    .build();

            scheduler.scheduleJob(job, trigger);
            scheduler.start();

            Assertions.assertThat(LatchJob.await(5, TimeUnit.SECONDS))
                    .as("Job 应在 5 秒内执行并 countDown")
                    .isTrue();
            Assertions.assertThat(scheduler.getMetaData().getNumberOfJobsExecuted()).isGreaterThanOrEqualTo(1);
        } finally {
            // true = 等待正在执行的 Job 结束再关闭
            scheduler.shutdown(true);
        }
    }

    /**
     * 测试专用 Job。public static + 无参构造，Quartz 默认 JobFactory 才能实例化。
     */
    public static class LatchJob implements Job {

        private static volatile CountDownLatch latch = new CountDownLatch(1);

        static void reset() {
            latch = new CountDownLatch(1);
        }

        static boolean await(long timeout, TimeUnit unit) throws InterruptedException {
            return latch.await(timeout, unit);
        }

        @Override
        public void execute(JobExecutionContext context) {
            latch.countDown();
        }
    }
}
