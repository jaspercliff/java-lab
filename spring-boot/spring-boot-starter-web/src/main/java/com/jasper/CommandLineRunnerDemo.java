package com.jasper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommandLineRunnerDemo implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        log.info("commandlineRunner is running");
        for (String arg : args) {
            System.out.println(arg);
        }
    }
}
