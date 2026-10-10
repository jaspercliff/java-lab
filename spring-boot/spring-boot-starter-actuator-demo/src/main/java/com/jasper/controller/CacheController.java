package com.jasper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cache")
@RequiredArgsConstructor
public class CacheController {

    // @Cacheable 会自动创建一个名为 "users" 的缓存
    @Cacheable(value = "users", key = "#id")
    @GetMapping("getUserById/{id}")
    public String getUserById(@PathVariable Long id) {
        // 模拟从数据库查数据
        return "User_" + id;
    }

    @Cacheable(value = "products", key = "#id")
    @GetMapping("getProductById/{id}")
    public String getProductById(@PathVariable Long id) {
        // 模拟从数据库查数据
        return "product_" + id;
    }
}
