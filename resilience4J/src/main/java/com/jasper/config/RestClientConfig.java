package com.jasper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient producerRestClient() {
        return RestClient.builder()
                .baseUrl("http://127.0.0.1:8081/producer")
//                .defaultUriVariables(Map.of("var", "foo"))
                .build();
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .defaultHeaders(headers -> {
                    headers.setAccept(
                            List.of(MediaType.APPLICATION_JSON)
                    );
                })
                .build();
    }
}