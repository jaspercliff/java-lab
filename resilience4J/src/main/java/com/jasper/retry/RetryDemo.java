package com.jasper.retry;

import com.jasper.exception.BusinessException;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Slf4j
public class RetryDemo {
    public static void main(String[] args) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3) //最大重试次数
                .waitDuration(Duration.ofMillis(1000)) //两次重试之间的时间间隔
//                .retryExceptions(IOException.class, TimeoutException.class) //配置一个Throwable类型的列表，被记录为失败类型，需要进行重试
                .ignoreExceptions(BusinessException.class) //配置一个Throwable类型的列表，被记录为忽略类型，不需要进行重试
                //如果连续失败，不一定每次都等待相同的时间。
                //可以使用指数退避
                .intervalFunction(
                        IntervalFunction.ofExponentialBackoff(
                                Duration.ofMillis(500),
                                2.0
                        )
                )
                .failAfterMaxAttempts(true) //配置是否在达到最大重试次数后抛出异常，默认为true
                .build();

        // 使用自定义的配置创建RetryRegistry
        RetryRegistry registry = RetryRegistry.of(config);

        // 使用默认的配置从Registry中获取和创建一个Retry
        Retry retry = registry.retry("backend");

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
