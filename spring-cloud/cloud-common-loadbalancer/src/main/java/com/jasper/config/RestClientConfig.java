package com.jasper.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalanced() {
        return RestClient.builder();
    }

    @Primary
    @Bean
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}