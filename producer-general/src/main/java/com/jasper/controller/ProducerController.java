package com.jasper.controller;

import com.jasper.result.ApiResult;
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
    public ApiResult<String> index() {
        log.info("producer index");
        return ApiResult.success("Hello World");
    }

    @GetMapping("/test")
    public ApiResult<String> test() {
        log.info("producer test");
        return ApiResult.success("test");
    }

    @GetMapping("/echo/{string}")
    public ApiResult<String> echo(@PathVariable String string) {
        log.info("producer echo {}", string);
        return ApiResult.success("hello " + string);
    }
}
