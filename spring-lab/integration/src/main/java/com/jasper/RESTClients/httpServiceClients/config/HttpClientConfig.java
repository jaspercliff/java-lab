package com.jasper.RESTClients.httpServiceClients.config;

import com.jasper.RESTClients.httpServiceClients.exchange.ProducerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpClientConfig {

    /**
     * 将 HTTP 服务定义为带有 @HttpExchange 方法的 Java 接口，
     * 并使用 HttpServiceProxyFactory 创建客户端代理，通过 RestClient 、 WebClient 或 RestTemplate 通过 HTTP 远程访问
     */
    @Bean
    ProducerClient producerClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:8081/producer")
                .build();

        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory.builderFor(
                        RestClientAdapter.create(restClient))
                    .build();

        return factory.createClient(ProducerClient.class);
    }
}