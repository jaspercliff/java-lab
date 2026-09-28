package com.jasper;

import com.jasper.config.ValueAnnoConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(1) // 如果有多个 Runner，可以使用 @Order 控制执行顺序
public class MyApplicationRunner implements ApplicationRunner {

    private final ValueAnnoConfig jasperConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("start====================");
        log.info(jasperConfig.getName());
        log.info(jasperConfig.getAge());
        log.info(jasperConfig.getHobby());
    }
}