package com.jasper.controller;

import com.jasper.exchange.ProducerClient;
import com.jasper.result.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("exchange")
@RequiredArgsConstructor
public class ExchangeController {
    private final ProducerClient producerClient;

    @GetMapping
    public ApiResponse<String> index() {
        return producerClient.index();
    }

    @GetMapping("/test")
    public ApiResponse<String> test() {
        return producerClient.test();
    }
    @GetMapping("/echo/{str}")
    public ApiResponse<String> echo(@PathVariable String str) {
        return producerClient.echo(str);
    }
}
