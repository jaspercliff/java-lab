package com.jasper.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/random")
public class RandomController {

    // 线程安全的计数器
    private final AtomicInteger counter = new AtomicInteger(0);
    private final Random random = new Random();

    /**
     * 测试接口：直接传参控制失败率
     * 1. 默认全部成功: http://localhost:18081/producer/test
     * 2. 指定 80% 失败率: http://localhost:18081/producer/test?rate=80
     */
    @GetMapping("/test")
    public String test(@RequestParam(value = "rate", defaultValue = "0") int rate) {
        int currentRate = Math.clamp(rate, 0, 100);

        // 如果设置了失败率，并且随机数落在失败区间内，就抛异常
        if (currentRate > 0 && random.nextInt(100) < currentRate) {
            throw new RuntimeException("Simulated error! Current rate: " + currentRate + "%");
        }
        
        return "Hello from Producer! Success. Rate: " + currentRate + "%";
    }

    @GetMapping("/absolute")
    public String absolute(@RequestParam(value = "rate", defaultValue = "0") int rate) {
        int currentRate = Math.clamp(rate, 0, 100);

        if (currentRate == 0) {
            return "Hello from Producer! Success. Rate: 0%";
        }
        if (currentRate == 100) {
            throw new RuntimeException("Simulated error! Current rate: 100%");
        }

//        long currentCount = counter.getAndIncrement();
        // 使用 AtomicInteger，超过 10000 自动重置为 0
        long currentCount = counter.getAndUpdate(val -> (val >= 10000) ? 0 : val + 1);

        // 如果是 50%，可以做到“完美交替”（一个成功，一个失败，绝对精准 50%）
        if (currentRate == 50) {
            if (currentCount % 2 == 0) {
                throw new RuntimeException("Simulated absolute error! (50% Alternating)");
            }
            return "Hello from Producer! Success. (50% Alternating)";
        }

        // 支持任意百分比（基于 100 个请求为一个周期）
        // 例如 rate = 30，则每 100 个请求中，前 30 个失败，后 70 个成功
        if ((currentCount % 100) < currentRate) {
            throw new RuntimeException("Simulated absolute error! Rate: " + currentRate + "%");
        }

        return "Hello from Producer! Success. Rate: " + currentRate + "%";
    }
}