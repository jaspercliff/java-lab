package com.jasper.feign;

import com.jasper.result.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 把 HTTP API 调用声明成 Java Interface，让远程调用看起来像调用本地方法。
 * feign 只要引入了loadbalancer会默认负载均横 不需要 like restClient 额外配置@loadbalanced
 */
@FeignClient(name = "producer-service",path = "producer") //OpenFeign 会把它交给服务发现组件（例如 Nacos）去解析
public interface ProducerServiceClient {

    @GetMapping
    ApiResponse<String> index();

    @GetMapping("/test")
    ApiResponse<String> test();

    @GetMapping("/echo/{string}")
    ApiResponse<String> echo(@PathVariable("string") String string);
}
