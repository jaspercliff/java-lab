package jasper.controller;

import com.jasper.result.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("consumer/restclient")
@RequiredArgsConstructor
public class ConsumerRestController {
    private final RestClient producerRestClient;

    @GetMapping
    public ApiResponse<String> index() {
        return producerRestClient.get()
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @GetMapping("/test")
    public ApiResponse<String> test() {
        return producerRestClient.get()
                .uri("/test")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @GetMapping("/echo/{str}")
    public ApiResponse<String> echo(@PathVariable String str) {
        return producerRestClient.get()
                .uri("/echo/{str}", str)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
