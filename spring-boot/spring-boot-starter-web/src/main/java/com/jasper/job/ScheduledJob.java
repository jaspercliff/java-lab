package com.jasper.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledJob {

    private final BusinessTask businessTask;

    /**
     * 每隔 2 秒触发一次
     */
//    @Scheduled(fixedRate = 2000)
    @Scheduled(cron = "@hourly")
    public void schedule() {

        log.info("触发定时任务，当前线程：{}", Thread.currentThread().getName());

        // 提交给独立线程池
        businessTask.execute();
    }
}