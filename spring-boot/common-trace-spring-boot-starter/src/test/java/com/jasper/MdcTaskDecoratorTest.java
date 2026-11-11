package com.jasper;

import com.jasper.trace.task.MdcTaskDecorator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MdcTaskDecoratorTest {

    private final MdcTaskDecorator decorator =
            new MdcTaskDecorator();

    @AfterEach
    void cleanup() {
        MDC.clear();
    }

    @Test
    void shouldPropagateMdc() {

        MDC.put("traceId", "trace-123");

        Runnable task = decorator.decorate(() -> {
            assertEquals("trace-123", MDC.get("traceId"));
        });

        MDC.clear();

        task.run();

        assertNull(MDC.get("traceId"));
    }

    @Test
    void shouldRestoreWorkerThreadMdc() {

        MDC.put("traceId", "parent");

        Runnable task = decorator.decorate(() -> {
            assertEquals("parent", MDC.get("traceId"));
        });

        MDC.put("traceId", "worker");
//        任务执行结束后，必须恢复工作线程原本的 MDC，而不是简单地清空 MDC。
        task.run();

        assertEquals("worker", MDC.get("traceId"));
    }
}