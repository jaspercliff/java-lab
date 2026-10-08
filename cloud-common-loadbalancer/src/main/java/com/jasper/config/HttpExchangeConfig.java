package com.jasper.config;

import com.jasper.exchange.ProducerClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpExchangeConfig {

    @Bean
    public ProducerClient producerClient(
            @Qualifier("loadBalanced")
            RestClient.Builder builder) {

        RestClient restClient = builder
                .baseUrl("http://producer-service-rest")
                .build();

        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory
                        .builderFor(RestClientAdapter.create(restClient))
                        .build();

        return factory.createClient(ProducerClient.class);
    }
}