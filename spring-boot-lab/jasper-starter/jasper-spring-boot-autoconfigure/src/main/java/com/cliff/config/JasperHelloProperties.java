package com.cliff.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@ConfigurationProperties(prefix = "jasper.hello")
@Getter
public class JasperHelloProperties {

    /**
     * 是否启用
     */
    private boolean enabled = false;

    /**
     * 默认名称
     */
    private String name = "Jasper";


}