package com.jasper.web.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/** Jackson 全局约定配置，前缀 {@code jasper.jackson}。 */
@ConfigurationProperties(prefix = "jasper.jackson")
public record JacksonProperties(

        /* 是否开启全局 Jackson 约定，默认开启。 */
        @DefaultValue("true")
        boolean enabled,

        /* 日期时间格式，默认 yyyy-MM-dd HH:mm:ss。 */
        @DefaultValue("yyyy-MM-dd HH:mm:ss")
        String datePattern,

        /* 时区，默认 Asia/Shanghai。 */
        @DefaultValue("Asia/Shanghai")
        String timeZone,

        /* Long / BigInteger 是否序列化为字符串，避免 JS 精度丢失，默认开启。 */
        @DefaultValue("true")
        boolean longToString
) {
}
