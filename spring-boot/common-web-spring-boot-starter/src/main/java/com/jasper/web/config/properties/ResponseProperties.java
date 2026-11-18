package com.jasper.web.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/** 成功响应自动包装配置，前缀 {@code jasper.web.response}。 */
@ConfigurationProperties(prefix = "jasper.web.response")
public record ResponseProperties(

        /* 是否开启 ApiResult.success 自动包装，默认开启。 */
        @DefaultValue("true")
        boolean enabled
) {
}
