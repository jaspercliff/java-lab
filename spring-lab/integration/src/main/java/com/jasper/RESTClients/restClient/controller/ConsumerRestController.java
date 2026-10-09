package com.jasper.RESTClients.restClient.controller;

import com.jasper.result.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("consumer/restclient")
@RequiredArgsConstructor
public class ConsumerRestController {
    private final RestClient producerRestClient;

    @GetMapping
    public ApiResponse<String> index() {
        return producerRestClient
                .get()
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @GetMapping("/test")
    public ApiResponse<String> test() {
        return producerRestClient.get()
// 用 URI 方法指定请求 URI。此步骤为可选步骤，如果 RestClient 配置为默认 URI，可以跳过
//        通常指定为字符串 ，并可选 URI 模板变量
//        URI 模板变量（URI Template Variables），简单来说，就是在 URL 中用大括号 {} 占位的动态变量
                .uri("/test")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @GetMapping("/echo/{str}")
    public ApiResponse<String> echo(@PathVariable String str) {
        return producerRestClient.get()
                .uri("/echo/{str}", str)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
