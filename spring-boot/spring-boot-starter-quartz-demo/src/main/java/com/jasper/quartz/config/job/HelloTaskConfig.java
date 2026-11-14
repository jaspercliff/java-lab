package com.jasper.quartz.config.job;

import com.jasper.quartz.config.QuartzDemoProperties;
import com.jasper.quartz.definition.QuartzTaskDefinition;
import com.jasper.quartz.job.HelloWorldJob;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelloTaskConfig implements QuartzTaskDefinition {
    private final QuartzDemoProperties properties;
    @Override
    public JobDetail jobDetail() {
        return JobBuilder.newJob(HelloWorldJob.class)
                .withIdentity("helloJob", properties.group())
                .withDescription("基础 Job：JobDataMap、fireTime、result")
                .usingJobData("message", "Hello from JobDetail")
                .usingJobData("jobLevelNumber", "42")
                .storeDurably()
                .build();
    }

    @Override
    public Trigger trigger() {
        return TriggerBuilder.newTrigger()
                .withIdentity("helloJob-trigger", properties.group())
                .forJob(jobDetail())
                .withDescription("每 10 秒执行一次")
                .withPriority(5)
                // Trigger 上的同名 key 会覆盖 JobDetail 上的值
                .usingJobData("message", "Hello from Trigger（覆盖 JobDetail 同名 key）")
                .usingJobData("triggerMessage", "trigger-only-data")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(10)
                        .withRepeatCount(4) // 总共5次 首次+4
                        // 错过的次数不补跑，从下一个计划点继续
                        .withMisfireHandlingInstructionNextWithRemainingCount())
                .build();
    }
}