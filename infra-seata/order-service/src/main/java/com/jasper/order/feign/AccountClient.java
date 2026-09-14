package com.jasper.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "account-service")
public interface AccountClient {

    @PostMapping("/account/debit")
    void debit(@RequestParam("userId") String userId, @RequestParam("money") int money);
}
