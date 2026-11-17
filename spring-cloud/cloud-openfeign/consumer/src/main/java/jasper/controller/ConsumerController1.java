package jasper.controller;

import com.jasper.result.ApiResult;
import jasper.feign.ProducerServiceClient1;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("consumer/defaultContract")
@RequiredArgsConstructor
public class ConsumerController1 {

    private final ProducerServiceClient1 producerServiceClient;

    @GetMapping
    public ApiResult<String> index() {
        return producerServiceClient.index();
    }

    @GetMapping("/test")
    public ApiResult<String> test() {
        return producerServiceClient.test();
    }
    @GetMapping("/echo/{str}")
    public ApiResult<String> echo(@PathVariable String str) {
        return producerServiceClient.echo(str);
    }
}
