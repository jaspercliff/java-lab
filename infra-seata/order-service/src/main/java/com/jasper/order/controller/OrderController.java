package com.jasper.order.controller;

import com.jasper.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/create")
    public String create(@RequestParam String userId, @RequestParam String commodityCode, @RequestParam int count) {
        orderService.create(userId, commodityCode, count);
        return "Order created successfully";
    }
}
