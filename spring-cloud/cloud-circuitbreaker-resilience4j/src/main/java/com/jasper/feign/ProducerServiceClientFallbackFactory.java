package com.jasper.feign;

import com.jasper.result.ApiResult;
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
            public ApiResult<String> index() {
                return ApiResult.success("fallback");
            }

            @Override
            public ApiResult<String> test() {
                return ApiResult.success("fallback");
            }

            @Override
            public ApiResult<String> echo(String string) {
                return ApiResult.success("fallback");
            }
        };
    }
}