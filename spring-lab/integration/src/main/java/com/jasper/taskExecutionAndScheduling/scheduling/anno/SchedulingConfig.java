package com.jasper.taskExecutionAndScheduling.scheduling.anno;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan(basePackages = "com.jasper.taskExecutionAndScheduling.scheduling.anno")
public class SchedulingConfig {
}