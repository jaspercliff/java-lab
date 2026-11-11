package com.jasper.taskExecutionAndScheduling.scheduling.anno;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class SchedulingApplication {

    public static void main(String[] args) {

        try (var context = new AnnotationConfigApplicationContext(
                             SchedulingConfig.class)) {

            log.info("Spring 容器启动成功");
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}