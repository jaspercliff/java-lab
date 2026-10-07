package com.jasper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * --name=jasper --age=18 hello world
 * CommandLineRunner：给你 String[]，你自己解析。
 * ApplicationRunner：Spring Boot 已经帮你把命令行参数解析好了
 */
@Slf4j
@Component
public class ApplicationRunnerDemo implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("ApplicationRunner is running");
        log.info(args.getOptionNames().toString());
        log.info(args.getOptionValues("name").toString());
        log.info(args.getOptionValues("age").toString());
        // 获取没有以 --xxx 这种形式传入的普通命令行参数
//        --name=jasper    → Option
//                --age=18         → Option
//        hello            → Non-option
//        world            → Non-option
        log.info(args.getNonOptionArgs().toString());
    }
}
