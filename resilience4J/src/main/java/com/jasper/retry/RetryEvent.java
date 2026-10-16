package com.jasper.retry;

import com.jasper.exception.BusinessException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Slf4j
public class RetryEvent {
    public static void main(String[] args) {
        RetryConfig config = RetryConfig.custom().maxAttempts(3) //最大重试次数
                .waitDuration(Duration.ofMillis(1000)) //两次重试之间的时间间隔
                .ignoreExceptions(BusinessException.class) //配置一个Throwable类型的列表，被记录为忽略类型，不需要进行重试
                .build();

        RetryRegistry registry = RetryRegistry.of(config);
        registry.getEventPublisher().onEntryAdded(
                event -> log.info("Retry added: {}", event.getAddedEntry().getName())
        ).onEntryRemoved(
                event -> log.info("Retry removed: {}", event.getRemovedEntry().getName())
        ).onEntryReplaced(
                event -> log.info("Retry replaced: {}", event.getNewEntry().getName())
        );
        Retry retry = registry.retry("backend");
        retry.getEventPublisher().onRetry(
                event -> log.info("Retry occurred: {}", event.getEventType())
                        )
                .onIgnoredError(
                event -> log.info("Retry ignored error ")
                )
                .onSuccess(
                        event -> log.info("Retry succeeded")
                )
                .onError(
                        event -> log.info("Retry failed")
                )

        ;
        AtomicInteger counter = new AtomicInteger();
        Supplier<String> supplier = () -> {
            int attempt = counter.incrementAndGet();
            log.info("第 {} 次调用", attempt);
            if (attempt < 3) {
                throw new RuntimeException("远程服务暂时不可用");
            }
            return "库存查询成功";
        };
        String s = retry.executeSupplier(supplier);
        log.info("最终结果: {}", s);
    }

}
