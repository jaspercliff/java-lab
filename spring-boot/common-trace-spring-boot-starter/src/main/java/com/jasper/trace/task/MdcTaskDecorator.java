package com.jasper.trace.task;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * MDC 上下文跨线程传播 的组件
 */
public class MdcTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(@NonNull Runnable runnable) {
//        这里复制的是整个 MDC Map
        // 提交任务时捕获调用线程的 MDC
        Map<String, String> capturedContext =
                MDC.getCopyOfContextMap();

        return () -> {

            // 保存工作线程原有 MDC
            Map<String, String> previousContext =
                    MDC.getCopyOfContextMap();

            try {
                if (capturedContext == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(capturedContext);
                }

                runnable.run();

            } finally {

                // 恢复工作线程原来的 MDC
                if (previousContext == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(previousContext);
                }
            }
        };
    }
}

