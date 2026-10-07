package com.jasper.RESTClients.httpServiceClients.exchange;

import org.springframework.web.service.annotation.GetExchange;

public interface ProducerClient {

    /**
     * see  method parameter website  <a href="https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-http-service-client-method-parameters">...</a>
     */
//    @HttpExchange(method = "GET", url = "/test")
    @GetExchange("/test")
    String test();
}