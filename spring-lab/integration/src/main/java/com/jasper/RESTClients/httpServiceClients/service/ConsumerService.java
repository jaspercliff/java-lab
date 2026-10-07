package com.jasper.RESTClients.httpServiceClients.service;

import com.jasper.RESTClients.httpServiceClients.exchange.ProducerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsumerService {

    private final ProducerClient producerClient;

    public String callProducer() {
        return producerClient.test();
    }
}