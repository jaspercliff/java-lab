package com.jasper.config;

import com.jasper.exchange.ProducerRandomClient;
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
    ProducerRandomClient producerClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:18081/random")
                .build();

        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory.builderFor(
                        RestClientAdapter.create(restClient))
                    .build();

        return factory.createClient(ProducerRandomClient.class);
    }
}