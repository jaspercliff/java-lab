package jasper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    /**
     * RestClient 是同步阻塞式 HTTP 客户端；
     * WebClient 是响应式、非阻塞式 HTTP 客户端
     * 高并发、I/O 密集、流式/响应式场景
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://127.0.0.1:8080/producer")
                .build();
    }
}