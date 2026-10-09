package com.jasper.controller;

import com.jasper.result.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequiredArgsConstructor
@RequestMapping("consumer")
public class ConsumerController {

    private final RestClient producerRestClient;

    @GetMapping
    @CircuitBreaker(
            name = "producerService",
            fallbackMethod = "fallback"
    )
    public ApiResponse<String> index() {
        return producerRestClient
                .get()
                // .uri("/")
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new RuntimeException("Producer service error status: " + response.getStatusCode());
                })
                .body(new ParameterizedTypeReference<ApiResponse<String>>() {
                });
    }
    @GetMapping("/test")
    @CircuitBreaker(
            name = "producerService",
            fallbackMethod = "fallback"
    )
    public ApiResponse<String> test() {
        throw new RuntimeException("模拟调用失败");
    }
    private ApiResponse<String> fallback(Throwable e) {
        return ApiResponse.success("producer-service unavailable");
    }
}
