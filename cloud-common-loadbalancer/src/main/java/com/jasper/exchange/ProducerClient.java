package com.jasper.exchange;

import com.jasper.result.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/producer")
public interface ProducerClient {

    @GetExchange
    ApiResponse<String> index();

    @GetExchange("/test")
    ApiResponse<String> test();

    @GetExchange("/echo/{str}")
    ApiResponse<String> echo(@PathVariable String str);
}