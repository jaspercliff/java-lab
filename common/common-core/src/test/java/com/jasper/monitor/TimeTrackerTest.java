package com.jasper.monitor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author jasper
 * @since 2026-07-18 <br>
 */
public class TimeTrackerTest {

    /** AutoCloseable */
    @Test
    public void test() throws InterruptedException {
        try (TimeTracker tracker = TimeTracker.start("http"); ) {
            Thread.sleep(1000);
        }
    }

    @Test
    public void testFunctional() {
        TimeTracker.measure(
                "redis",
                () -> {
                    Thread.sleep(1000);
                });
    }

    /** lambda 抛异常时应原封不动透传（sneakyThrow），且 tracker 标记为失败 */
    @Test
    public void testFunctional1() {
        assertThatThrownBy(() -> TimeTracker.measure(
                "redis",
                () -> {
                    Thread.sleep(10);
                    throw new RuntimeException("boom");
                }))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("boom");
    }

    @Test
    public void testFunctional2() {
        String measure =
                TimeTracker.measure(
                        "redis",
                        () -> {
                            Thread.sleep(1000);
                            return "jasper";
                        });
        System.out.println(measure);
    }

    @Test
    public void testNested() {
        TimeTracker.measure(
                "redis",
                () -> {
                    Thread.sleep(1000);
                    TimeTracker.measure(
                            "db",
                            () -> {
                                Thread.sleep(1000);
                                TimeTracker.measure(
                                        "db1",
                                        () -> {
                                            Thread.sleep(1000);
                                        });
                            });
                });
    }

    /** 静默模式只计时，不打日志、不改动嵌套层级，供访问日志复用。 */
    @Test
    public void silentModeMeasuresWithoutTouchingLevel() throws InterruptedException {
        try (TimeTracker silent = TimeTracker.silent("access")) {
            Thread.sleep(30);
            assertThat(silent.elapsedMillis()).isGreaterThanOrEqualTo(20);
        }
    }
}
