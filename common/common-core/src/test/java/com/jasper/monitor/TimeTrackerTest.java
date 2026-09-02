package com.jasper.monitor;

import org.junit.jupiter.api.Test;

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

    /** lambda void return test fail */
    @Test
    public void testFunctional1() {
        TimeTracker.measure(
                "redis",
                () -> {
                    Thread.sleep(1000);
                    throw new RuntimeException();
                });
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
}
