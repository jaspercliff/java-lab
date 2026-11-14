package com.jasper.quartz;

import org.junit.jupiter.api.Test;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 纯单元测试：Cron 表达式解析与下一次触发时间计算。
 *
 * <p>这类测试不启动 Spring，速度极快，适合在 CI 中大量覆盖 Cron 边界。
 */
class CronExpressionTest {

    @Test
    void weekdaysAtNineShouldSkipWeekend() throws ParseException {
        // 工作日每天 09:00（Quartz 6 段：秒 分 时 日 月 周）
        CronExpression cron = new CronExpression("0 0 9 ? * MON-FRI");
        cron.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        // 2026-11-13 是周五，09:00 CST = 01:00Z；下一次应该是周一 2026-11-16 09:00 CST
        Date next = cron.getNextValidTimeAfter(Date.from(Instant.parse("2026-11-13T01:00:00Z")));

        Assertions.assertThat(next.toInstant()).isEqualTo(Instant.parse("2026-11-16T01:00:00Z"));
    }

    @Test
    void everyFifteenMinutesShouldReturnOrderedTimes() throws ParseException {
        CronExpression cron = new CronExpression("0 0/15 * * * ?");
        cron.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        Date first = cron.getNextValidTimeAfter(Date.from(Instant.parse("2026-11-11T00:00:00Z")));
        Date second = cron.getNextValidTimeAfter(first);

        Assertions.assertThat(first.toInstant()).isEqualTo(Instant.parse("2026-11-11T00:15:00Z"));
        Assertions.assertThat(second.toInstant()).isEqualTo(Instant.parse("2026-11-11T00:30:00Z"));
    }

    @Test
    void invalidExpressionShouldThrowParseException() {
        Assertions.assertThatThrownBy(() -> new CronExpression("0 0 25 * * ?"))
                .isInstanceOf(ParseException.class);
    }
}
