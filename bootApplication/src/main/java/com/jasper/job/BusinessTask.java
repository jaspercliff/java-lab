package com.jasper.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BusinessTask {

    @Async("taskExecutor")
    public void execute() {

        log.info("开始执行业务，当前线程：{}", Thread.currentThread().getName());

        try {
            // 模拟耗时业务，执行 10 秒
            Thread.sleep(10000);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("业务任务被中断", e);
            return;
        }

        log.info("业务执行完成");
    }
}