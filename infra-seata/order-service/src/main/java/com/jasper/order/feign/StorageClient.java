package com.jasper.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "storage-service")
public interface StorageClient {

    @PostMapping("/storage/deduct")
    void deduct(@RequestParam("commodityCode") String commodityCode, @RequestParam("count") int count);
}
