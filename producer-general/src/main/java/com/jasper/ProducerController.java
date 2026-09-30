package com.jasper;

import com.jasper.result.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("producer")
public class ProducerController {
    @GetMapping
    public ApiResponse<String> index() {
        return ApiResponse.success("Hello World");
    }

    @GetMapping("/test")
    public ApiResponse<String> test() {
        return ApiResponse.success("test");
    }

    @GetMapping("/echo/{string}")
    public ApiResponse<String> echo(@PathVariable String string) {
        return ApiResponse.success("hello " + string);
    }
}
