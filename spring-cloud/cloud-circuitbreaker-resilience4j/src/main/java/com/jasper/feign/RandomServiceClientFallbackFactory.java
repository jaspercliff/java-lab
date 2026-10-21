package com.jasper.feign;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RandomServiceClientFallbackFactory
        implements FallbackFactory<RandomServiceClient> {

    @Override
    public RandomServiceClient create(Throwable cause) {

        // 判断异常原因，生成不同的降级提示
        String reason = classify(cause);

        if (reason.equals("CIRCUIT_BREAKER")) {
            log.warn("RandomServiceClient 熔断降级", cause);
        } else {
            log.error("RandomServiceClient 调用失败，普通降级", cause);
        }

        // 返回统一接口的降级实现，但不同原因返回不同结果
        return new RandomServiceClient() {

            @Override
            public String test() {
                return buildFallbackMessage("test", reason);
            }

            @Override
            public String absolute() {
                return buildFallbackMessage("absolute", reason);
            }
        };
    }

    /**
     * 识别触发降级的异常。
     * 同时检查 cause 本身和它的异常链。
     */
    private String classify(Throwable cause) {
        Throwable current = cause;

        while (current != null) {
            if (current instanceof CallNotPermittedException) {
                return "CIRCUIT_BREAKER";
            }

            current = current.getCause();
        }

        return "NORMAL_FAILURE";
    }

    /**
     * 根据降级原因返回不同的响应。
     */
    private String buildFallbackMessage(String method, String reason) {
        if (reason.equals("CIRCUIT_BREAKER")) {
            return "CircuitBreaker fallback: " + method
                    + "() rejected because circuit is OPEN.";
        }else {
            return "Normal fallback: " + method
                    + "() failed because the downstream call failed.";
        }
    }
}