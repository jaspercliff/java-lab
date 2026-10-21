package com.jasper.feign;

import com.jasper.result.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProducerServiceClientFallbackFactory
        implements FallbackFactory<ProducerServiceClient> {

    @Override
    public ProducerServiceClient create(Throwable cause) {
        log.error("Feign 调用失败，触发降级", cause);

        return new ProducerServiceClient() {

            @Override
            public ApiResponse<String> index() {
                return ApiResponse.success("fallback");
            }

            @Override
            public ApiResponse<String> test() {
                return ApiResponse.success("fallback");
            }

            @Override
            public ApiResponse<String> echo(String string) {
                return ApiResponse.success("fallback");
            }
        };
    }
}