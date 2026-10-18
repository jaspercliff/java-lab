package com.jasper.bulkhead;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
public class BulkheadDemo {
    public static void main(String[] args) {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(2) //允许多少线程并发执行
                .maxWaitDuration(Duration.ofMillis(500)) //当达到并发调用数量时，新的线程执行时将被阻塞，这个属性表示最长的等待时间
                .build();

        BulkheadRegistry bulkheadRegistry = BulkheadRegistry.of(config);
        Bulkhead backend = bulkheadRegistry.bulkhead("backend");

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
                String result = backend.executeSupplier(supplier);
                log.info("Result: {}", result);
            }).start();
        }
    }
}
