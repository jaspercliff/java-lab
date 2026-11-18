package com.jasper.web.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

/**
 * 错误文案国际化配置，前缀 {@code jasper.web.i18n}。
 *
 * <p>业务码与系统码放在不同 basename，互不覆盖：
 * 系统码用 SYS400 这类 key，业务码用 RB1001 这类 key。
 */
@ConfigurationProperties(prefix = "jasper.web.i18n")
public record I18nProperties(

        /* 按顺序解析的 basename，先系统后业务。 */
        @DefaultValue({"i18n/system", "i18n/business"})
        List<String> basenames,

        /* 资源文件编码，默认 UTF-8，支持直接写中文。 */
        @DefaultValue("UTF-8")
        String encoding
) {
}
