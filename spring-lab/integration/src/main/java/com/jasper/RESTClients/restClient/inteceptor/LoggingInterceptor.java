package com.jasper.RESTClients.restClient.inteceptor;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Slf4j
public class LoggingInterceptor
        implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte @NonNull [] body,
            ClientHttpRequestExecution execution)
            throws IOException {

        log.info("{} {}", request.getMethod(), request.getURI());

        return execution.execute(request, body);
    }
}