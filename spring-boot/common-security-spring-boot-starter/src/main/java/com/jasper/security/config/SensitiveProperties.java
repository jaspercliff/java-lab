package com.jasper.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

/**
 * 敏感字段关键字配置，前缀 {@code jasper.security.sensitive}。
 *
 * <p>字段名（含级联路径，如 {@code address.password}）忽略大小写后包含任意关键字即命中。
 * 访问日志与校验异常的 rejectedValue 共用这一份配置。
 */
@ConfigurationProperties(prefix = "jasper.security.sensitive")
public record SensitiveProperties(

        /* 命中后值统一脱敏；手机号/身份证号会按更友好的格式保留尾号。 */
        @DefaultValue({"password", "pwd", "token", "secret", "accessKey",
                "authorization", "credentials", "phone", "mobile", "idCard"})
        List<String> fields
) {
}
