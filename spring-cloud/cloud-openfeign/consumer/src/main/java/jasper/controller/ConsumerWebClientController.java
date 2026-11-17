package jasper.controller;

import com.jasper.result.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("consumer/webclient")
@RequiredArgsConstructor
public class ConsumerWebClientController {
    private final WebClient webClient;

    @GetMapping
    public Mono<ApiResult<String>> index() {
        return webClient.get()
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResult<String>>() {});
    }

    @GetMapping("/test")
    public Mono<ApiResult<String>> test() {
        return webClient.get()
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResult<String>>() {});
    }

    @GetMapping("/echo/{str}")
    public Mono<ApiResult<String>> echo(@PathVariable String str) {
        return webClient.get()
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResult<String>>() {});
    }
}
