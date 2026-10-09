package jasper.controller;

import com.jasper.result.ApiResponse;
import jasper.feign.ProducerServiceClient2;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("consumer/mvcContract")
@RequiredArgsConstructor
public class ConsumerController2 {

    private final ProducerServiceClient2 producerServiceClient;

    @GetMapping
    public ApiResponse<String> index() {
        return producerServiceClient.index();
    }

    @GetMapping("/test")
    public ApiResponse<String> test() {
        return producerServiceClient.test();
    }
    @GetMapping("/echo/{str}")
    public ApiResponse<String> echo(@PathVariable String str) {
        return producerServiceClient.echo(str);
    }
}
