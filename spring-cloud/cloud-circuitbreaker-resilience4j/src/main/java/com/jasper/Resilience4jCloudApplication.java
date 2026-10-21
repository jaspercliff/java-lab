package com.jasper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class Resilience4jCloudApplication {
    public static void main(String[] args) {
        SpringApplication.run(Resilience4jCloudApplication.class, args);
    }
}
