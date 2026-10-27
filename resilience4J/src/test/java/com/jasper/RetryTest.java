package com.jasper;

import io.github.resilience4j.core.functions.CheckedSupplier;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RetryTest {

    interface HelloWorldService {
        String sayHelloWorld() throws WebServiceException;
    }

    static class WebServiceException extends RuntimeException {
        WebServiceException(String message) {
            super(message);
        }
    }

    @Test
    void retryExample() {
        // Arrange：模拟服务每次调用都抛出异常
        HelloWorldService helloWorldService =
                mock(HelloWorldService.class);

        when(helloWorldService.sayHelloWorld())
                .thenThrow(new WebServiceException("BAM!"));

        // Arrange：配置最多调用 3 次，重试间隔为 0
        Retry retry = Retry.of(
                "id",
                RetryConfig.custom()
                        .maxAttempts(3)
                        .waitDuration(Duration.ZERO)
                        .build()
        );

        // Arrange：使用 Retry 装饰服务调用
        CheckedSupplier<String> retryableSupplier =
                Retry.decorateCheckedSupplier(
                        retry,
                        helloWorldService::sayHelloWorld
                );

        // Act + Assert：执行调用，验证最终抛出异常
        WebServiceException exception = assertThrows(
                WebServiceException.class,
                retryableSupplier::get
        );

        assertEquals("BAM!", exception.getMessage());

        // Assert：验证服务总共调用 3 次
        verify(helloWorldService, times(3))
                .sayHelloWorld();
    }
}