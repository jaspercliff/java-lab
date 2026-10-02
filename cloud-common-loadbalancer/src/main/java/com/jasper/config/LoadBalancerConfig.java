package com.jasper.config;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.List;

@Configuration
public class LoadBalancerConfig {

    /**
     * 配置多个固定实例
     */
    @Bean
    ServiceInstanceListSupplier serviceInstanceListSupplier() {

        List<ServiceInstance> instances = List.of(
                new DefaultServiceInstance(
                        "producer-8081",
                        "producer-service",
                        "127.0.0.1",
                        8081,
                        false
                ),
                new DefaultServiceInstance(
                        "producer-8082",
                        "producer-service",
                        "127.0.0.1",
                        8082,
                        false
                ),
                new DefaultServiceInstance(
                        "producer-8083",
                        "producer-service",
                        "127.0.0.1",
                        8083,
                        false
                )
        );

        return new ServiceInstanceListSupplier() {

            @Override
            public String getServiceId() {
                return "producer-service";
            }

            @Override
            public Flux<List<ServiceInstance>> get() {
                return Flux.just(instances);
            }
        };
    }
}