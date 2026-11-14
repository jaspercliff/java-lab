package com.jasper.quartz.bootstrap;

import com.jasper.quartz.definition.QuartzTaskDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuartzTaskRegister implements ApplicationRunner {

    private final Scheduler scheduler;

    private final List<QuartzTaskDefinition> definitions;

    @Override
    public void run(ApplicationArguments args) {
        for (QuartzTaskDefinition definition : definitions) {
            try {
                register(definition);
            } catch (SchedulerException | RuntimeException e) {
                log.error("Quartz 任务注册失败，definition={}",
                        definition.getClass().getName(), e);

                throw new IllegalStateException(
                        "Quartz 内置任务注册失败，应用无法正常启动: "
                                + definition.getClass().getName(),
                        e
                );
            }
        }
    }

    private void register(QuartzTaskDefinition definition)
            throws SchedulerException {

        // JobDetail / Trigger 构建错误也会在这里向上抛出
        JobDetail job = definition.jobDetail();
        Trigger trigger = definition.trigger();

        if (!trigger.getJobKey().equals(job.getKey())) {
            throw new IllegalArgumentException(
                    "Trigger 关联的 JobKey 与 JobDetail 不一致: "
                            + trigger.getKey()
            );
        }

        JobKey jobKey = job.getKey();
        TriggerKey triggerKey = trigger.getKey();

        boolean jobExists = scheduler.checkExists(jobKey);
        boolean triggerExists = scheduler.checkExists(triggerKey);

        // 1. Job 和 Trigger 都不存在
        if (!jobExists && !triggerExists) {
            scheduler.scheduleJob(job, trigger);
            log.info("Job 和 Trigger 已注册: {} -> {}",
                    jobKey, triggerKey);
            return;
        }

        // 2. Trigger 已存在，但 Job 不存在
        if (!jobExists) {
            throw new SchedulerException(
                    "Trigger 已存在，但关联 Job 不存在: " + triggerKey
            );
        }

        // 3. Job 已存在，检查并更新 JobDetail
        JobDetail oldJob = scheduler.getJobDetail(jobKey);

        JobDetail updatedJob = JobBuilder.newJob(job.getJobClass())
                .withIdentity(jobKey)
                .withDescription(job.getDescription())
                .usingJobData(oldJob.getJobDataMap())
                .storeDurably(job.isDurable())
                .requestRecovery(job.requestsRecovery())
                .build();

        scheduler.addJob(updatedJob, true, true);

        log.info("Job 已更新: {}", jobKey);

        // 4. 更新或补充 Trigger
        if (triggerExists) {
            scheduler.rescheduleJob(triggerKey, trigger);

            log.info("Trigger 已更新: {}", triggerKey);
        } else {
            scheduler.scheduleJob(trigger);

            log.info("Trigger 已补充: {}", triggerKey);
        }
    }
}