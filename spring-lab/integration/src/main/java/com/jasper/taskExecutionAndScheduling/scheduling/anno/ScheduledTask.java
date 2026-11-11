package com.jasper.taskExecutionAndScheduling.scheduling.anno;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * see also ScheduledExecutorDemo
 * 多个定时任务：使用 ThreadPoolTaskScheduler，避免单线程相互影响
 */
@Slf4j
@Component
public class ScheduledTask {

    //适合需要固定频率触发的任务
    @Scheduled(fixedRate = 2000)
    public void execute() {
        log.info("fixRate定时任务执行");
    }

    @Scheduled(fixedRate = 2, timeUnit = TimeUnit.SECONDS)
    public void execute1() {
        log.info("fixRate1定时任务执行");
    }
    //上一次任务执行完成后，等待 3 秒，再执行下一次任务
    //适合轮询任务、定期扫描数据库
    @Scheduled(fixedDelay = 3000)
    public void executeWithFixedDelay() {
        log.info("fixedDelay定时任务执行");
    }

    @Scheduled(cron = "@hourly")
    public void executeWithCron() {
        log.info("cron定时任务执行");
    }

    // initialDelay: 延迟多久后开始执行一次任务
    @Scheduled(initialDelay = 1000)
    public void execute2() {
        log.info("initialDelay 任务执行一次");
    }

    // 每天凌晨 2 点执行
    @Scheduled(cron = "0 0 2 * * ?")
    public void cronTask() {
        log.info("执行每日数据清理");
    }
}