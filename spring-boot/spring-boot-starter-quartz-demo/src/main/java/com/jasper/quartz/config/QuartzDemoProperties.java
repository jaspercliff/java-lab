package com.jasper.quartz.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * <p>对应 application.yml：
 * <pre>{@code
 * quartz-demo:
 *   bootstrap: true          # 是否在启动时注册演示任务
 *   group: demo              # 演示任务默认分组
 *   holiday-calendar: cnHolidays
 *   time-zone: Asia/Shanghai
 * }</pre>
 *
 * <p>使用 record + 构造器绑定（Spring Boot 3 推荐），{@link DefaultValue} 提供缺省值。
 * 注意：使用构造器绑定时不要再用 {@code @Component}，由 {@code @EnableConfigurationProperties} 注册即可。
 */
@ConfigurationProperties(prefix = "quartz-demo")
public record QuartzDemoProperties(

        //是否在应用启动时自动注册演示 Job（false 时可通过 REST 手动创建）。
        @DefaultValue("true") boolean bootstrap,

        // 演示 Job 的默认分组，等价于 Quartz 的 group 概念。
        @DefaultValue("demo") String group,

        // 节假日 Calendar 的名称，必须与 {@code QuartzConfig} 中注册的 Calendar Bean 名称一致。
        @DefaultValue("cnHolidays") String holidayCalendar,

        // Cron 表达式与 API 输出使用的默认时区。
        @DefaultValue("Asia/Shanghai") String timeZone
) {
}
