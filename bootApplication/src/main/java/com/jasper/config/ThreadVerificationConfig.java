package com.jasper.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
class ThreadVerificationConfig {

    /**

     * see also {@link com.jasper.controller.ThreadController#info()}
     */
    @Bean
    ApplicationRunner verifyThread() {
        return args -> {
            Thread thread = Thread.currentThread();
            log.info("Thread = {}", thread);
            log.info("isVirtual = {}", thread.isVirtual());//false
        };
    }
}