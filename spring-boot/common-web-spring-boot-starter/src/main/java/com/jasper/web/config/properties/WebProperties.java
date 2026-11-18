package com.jasper.web.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * common-web starter 的可配置项。
 *
 * <p>配置前缀：{@code jasper.web}
 */
@ConfigurationProperties(prefix = "jasper.web")
public record WebProperties(

        /*
          是否开启全局异常处理，默认开启。
          也可以直接通过 {@code jasper.web.enabled=false} 关闭自动配置。
         */
        @DefaultValue("true")
        boolean enabled,

        /*
          500 响应是否把异常原始 message 暴露给客户端。
          默认关闭，避免泄露内部实现细节；服务端日志始终打印完整堆栈。
         */
        @DefaultValue("false")
        boolean exposeExceptionMessage
) {
}
