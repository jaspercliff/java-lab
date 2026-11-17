package com.jasper.feign;

import com.jasper.result.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 把 HTTP API 调用声明成 Java Interface，让远程调用看起来像调用本地方法。
 * see also
 */
@FeignClient(name = "producer-service",path = "producer") //OpenFeign 会把它交给服务发现组件（例如 Nacos）去解析
public interface ProducerServiceClient {

    @GetMapping
    ApiResult<String> index();

    @GetMapping("/test")
    ApiResult<String> test();

    @GetMapping("/echo/{string}")
    ApiResult<String> echo(@PathVariable("string") String string);
}
