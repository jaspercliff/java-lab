package com.jasper.controller;

import com.jasper.feign.ProducerServiceClient;
import com.jasper.result.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("consumer")
@RequiredArgsConstructor
public class ConsumerController {

    private final ProducerServiceClient producerServiceClient;

    @GetMapping
    public ApiResponse<String> index() {
        return producerServiceClient.index();
    }

    @GetMapping("/test")
    public ApiResponse<String> test() {
        return producerServiceClient.test();
    }
    @GetMapping("/echo/{str}")
    public ApiResponse<String> echo(@PathVariable String str) {
        return producerServiceClient.echo(str);
    }
}
