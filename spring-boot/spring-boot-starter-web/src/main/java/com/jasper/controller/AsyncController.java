package com.jasper.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.CompletableFuture;

@RestController
@Slf4j
public class AsyncController {

    /**
     * filter 进俩遍
     */
    @GetMapping("/asyncFilterTest")
    public CompletableFuture<String> asyncTest() {
        log.info("~~~ [Controller] 主线程执行: {}", Thread.currentThread().getName());

        // 异步执行耗时任务（例如远程调用、查数据库等）
        // 这里dispatcherType=request，filter只会进一次 traceId会丢失
        return CompletableFuture.supplyAsync(() -> {
            log.info("~~~ [Async Thread] 后台线程开始工作: {}", Thread.currentThread().getName());
            try {
                Thread.sleep(1000); // 模拟耗时 1 秒
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.info("~~~ [Async Thread] 后台线程工作完成: {}", Thread.currentThread().getName());
            return "Async Result Success!";
        });
    }
}