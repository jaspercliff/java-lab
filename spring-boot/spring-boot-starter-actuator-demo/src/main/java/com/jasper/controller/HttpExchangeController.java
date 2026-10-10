package com.jasper.controller;

import com.jasper.exchange.ProducerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("exchange")
@RequiredArgsConstructor
public class HttpExchangeController {

    private final ProducerClient producerClient;

    @GetMapping
    public String exchange() {
        return producerClient.test();
    }
}
