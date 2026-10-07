package com.jasper.RESTClients.httpServiceClients.controller;

import com.jasper.RESTClients.httpServiceClients.service.ConsumerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConsumerController {

    private final ConsumerService consumerService;


    @GetMapping("/call-producer")
    public String callProducer() {
        return consumerService.callProducer();
    }
}