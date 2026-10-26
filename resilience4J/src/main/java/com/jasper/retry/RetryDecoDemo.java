package com.jasper.retry;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RetryDecoDemo {

    record Response(int status, String message) {
    }

    public static void main(String[] args) {
        RetryConfig config = RetryConfig.<Response>custom()
                .maxAttempts(3) //最大重试次数
                .waitDuration(Duration.ofMillis(1000)) //两次重试之间的时间间隔
                .retryOnResult(response -> response.status() == 500) //配置一个Predicate，用于判断结果是否需要重试
                .build();
        RetryRegistry retryRegistry = RetryRegistry.of(config);
        Retry retry = retryRegistry.retry("backend");
        AtomicInteger count = new AtomicInteger();
        Response result = Retry.decorateSupplier(retry, () -> {
            int attempt = count.incrementAndGet();

            log.info("第 {} 次调用", attempt);

            if (attempt < 3) {
                return new Response(500, "服务器异常");
            }

            return new Response(200, "成功");
        }).get();

        log.info("最终结果：{}", result);
    }
}
