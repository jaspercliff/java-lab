package com.jasper.exchange;

import org.springframework.web.service.annotation.GetExchange;

public interface ProducerRandomClient {

    @GetExchange("/absolute?rate=50")
    String absolute();
}