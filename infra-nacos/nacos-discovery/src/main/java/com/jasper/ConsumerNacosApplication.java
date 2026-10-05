package com.jasper;

import com.jasper.config.LoadBalancerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@LoadBalancerClient(
        name = "producer-service",
        configuration = LoadBalancerConfiguration.class
)
public class ConsumerNacosApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerNacosApplication.class, args);
    }
}

