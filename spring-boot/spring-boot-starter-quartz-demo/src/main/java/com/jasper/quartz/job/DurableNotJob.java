package com.jasper.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

@Slf4j
public class DurableNotJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        log.info("没有trigger会被删除");
    }
}
