package com.jasper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ActuatorApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ActuatorApplication.class);
//        在启动时“开启录像/插桩”，将所有启动步骤（如 Bean 实例化、环境准备等）记录到内存缓冲区中供 Actuator 查询
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
    }
}
