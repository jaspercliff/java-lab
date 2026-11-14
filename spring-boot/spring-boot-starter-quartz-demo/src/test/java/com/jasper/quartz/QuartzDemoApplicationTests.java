package com.jasper.quartz;

import com.jasper.quartz.service.QuartzJobService;
import org.junit.jupiter.api.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Spring 上下文冒烟测试：验证 Quartz 自动配置、自定义 Listener/JobFactory、Service 装配都正常。
 *
 * <p>测试关闭了任务自动注册，避免后台任务产生日志/副作用；
 * 同时关闭 Scheduler 自动启动，测试只验证“能装配”，不依赖时间。
 *
 * <p>使用独立的 test profile，避免本地把 {@code spring.profiles.active} 改成 jdbc 后，
 * 单元测试意外依赖 MySQL。test profile 下使用 RAMJobStore。
 */
@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "quartz-demo.bootstrap=false",
                "spring.quartz.auto-startup=false"
        })
class QuartzDemoApplicationTests {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private QuartzJobService jobService;

    @Test
    void contextLoadsWithQuartzScheduler() throws SchedulerException {
        Assertions.assertThat(scheduler).isNotNull();
        Assertions.assertThat(scheduler.getSchedulerName()).isEqualTo("quartz-demo-scheduler");
        Assertions.assertThat(scheduler.getMetaData().getJobStoreClass().getName())
                .contains("RAMJobStore");
    }

    @Test
    void cronPreviewShouldReturnRequestedCount() {
        var preview = jobService.previewCron("0 0 9 * * ?", "Asia/Shanghai", 3);

        assertThat(preview.expression()).isEqualTo("0 0 9 * * ?");
        assertThat(preview.nextFireTimes()).hasSize(3);
    }
}
