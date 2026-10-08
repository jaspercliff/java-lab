package com.jasper.controller;

import com.jasper.result.ApiResponse;
import jakarta.annotation.Resource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("consumer/restclient")
public class ConsumerRestController {

    @Resource
    private  RestClient.Builder loadBalanced;
    @Resource
    private  RestClient.Builder restClientBuilder;

    @GetMapping("index1")
    public ApiResponse<String> index1() {
        return restClientBuilder
                .build()
                .get()
                .uri("http://127.0.0.1:8081/producer")
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<String>>() {
                });
    }
    @GetMapping
    public ApiResponse<String> index() {
        return loadBalanced
                .build()
                .get()
                .uri("http://producer-service-rest/producer")
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<String>>() {
                });
    }

    @GetMapping("/test")
    public ApiResponse<String> test() {
        return loadBalanced.build().get()
                .uri("http://producer-service-rest/producer/test")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @GetMapping("/echo/{str}")
    public ApiResponse<String> echo(@PathVariable String str) {
        return loadBalanced.build().get()
                .uri("http://producer-service-rest/producer/echo/{str}", str)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
