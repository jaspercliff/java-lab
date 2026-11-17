package com.jasper.exchange;

import com.jasper.result.ApiResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/producer")
public interface ProducerClient {

    @GetExchange
    ApiResult<String> index();

    @GetExchange("/test")
    ApiResult<String> test();

    @GetExchange("/echo/{str}")
    ApiResult<String> echo(@PathVariable String str);
}