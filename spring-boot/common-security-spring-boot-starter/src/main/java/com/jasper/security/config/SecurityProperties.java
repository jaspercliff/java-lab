package com.jasper.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/** security 模块总开关，前缀 {@code jasper.security}。 */
@ConfigurationProperties(prefix = "jasper.security")
public record SecurityProperties(

        @DefaultValue("true")
        boolean enabled
) {
}
