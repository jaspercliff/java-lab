package com.jasper;

import com.jasper.result.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("producer")
public class ProducerController {
    @GetMapping
    public ApiResponse<String> index() {
        log.info("producer index");
        return ApiResponse.success("Hello World");
    }

    @GetMapping("/test")
    public ApiResponse<String> test() {
        log.info("producer test");
        return ApiResponse.success("test");
    }

    @GetMapping("/echo/{string}")
    public ApiResponse<String> echo(@PathVariable String string) {
        log.info("producer echo {}", string);
        return ApiResponse.success("hello " + string);
    }
}
