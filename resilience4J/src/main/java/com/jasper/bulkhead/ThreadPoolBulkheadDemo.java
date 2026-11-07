package com.jasper.bulkhead;

import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadConfig;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

/**
 * 默认就是基于信号量的实现，不是线程池隔离
 */
@Slf4j
public class ThreadPoolBulkheadDemo {
    public static void main(String[] args) {
        ThreadPoolBulkheadConfig config =
                ThreadPoolBulkheadConfig.custom()
                        .maxThreadPoolSize(2)
                        .coreThreadPoolSize(2)
                        .queueCapacity(2)
                        .keepAliveDuration(Duration.ofSeconds(20))
                        .build();
        ThreadPoolBulkheadRegistry registry = ThreadPoolBulkheadRegistry.of(config);

        ThreadPoolBulkhead backend = registry.bulkhead("backend");

        Supplier<String> supplier = ()->{
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("Hello Bulkhead");
            return "query success";
        };
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                CompletionStage<String> result =
                        backend.executeSupplier(supplier);
                result.whenComplete((value, throwable) -> {
                    log.info("Result: {}", value);
                });
            }).start();
        }
    }
}
