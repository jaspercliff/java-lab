package com.thread;

import com.jasper.constants.ThreadType;
import com.jasper.thread.ExecutorServiceFactory;
import com.thread.extension.ThreadModeExtension;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@ExtendWith(ThreadModeExtension.class)
public final class ExecutorServiceFactoryTest {

    /**
     * get thread type 是否正确
     */
    @TestTemplate
    void shouldDetectCorrectThreadTypeBasedOnConfiguration(ThreadType threadType) {
        log.info("Running shouldDetectCorrectThreadTypeBasedOnConfiguration in {}", threadType);

        ThreadType detected = ExecutorServiceFactory.getThreadType();

        if (threadType == ThreadType.VIRTUAL) {
            assertThat(detected)
                    .as("ExecutorServiceFactory should detect virtual thread mode when configured in %s", threadType)
                    .isEqualTo(ThreadType.VIRTUAL);
        } else {
            assertThat(detected)
                    .as("ExecutorServiceFactory should detect platform thread mode when not configured in %s", threadType)
                    .isEqualTo(ThreadType.PLATFORM);
        }

        log.info("Thread type detection test passed in {} - Thread type: {}", threadType, detected);
    }

    /**
     *  测试生成的线程类型是否正确
     */
    @TestTemplate
    void scheduledExecutorShouldProduceCorrectThreadType(ThreadType threadType) throws Exception {
        log.info("Running scheduledExecutorShouldProduceCorrectThreadType in {}", threadType);

        ScheduledExecutorService executor =
                ExecutorServiceFactory.newSingleThreadScheduledExecutor("executor-test-" + threadType);

        Future<Boolean> isVirtual = executor.submit(() -> Thread.currentThread().isVirtual());

        try {
            boolean taskRanOnVirtualThread = isVirtual.get(1, TimeUnit.SECONDS);

            if (threadType == ThreadType.VIRTUAL) {
                Assertions.assertThat(taskRanOnVirtualThread)
                        .as("Task should run on a virtual thread when configured in %s", threadType)
                        .isTrue();
            } else {
                Assertions.assertThat(taskRanOnVirtualThread)
                        .as("Task should run on a platform thread by default in %s", threadType)
                        .isFalse();
            }

            log.info("Scheduled executor thread type test passed in {} - Virtual thread: {}",
                    threadType, taskRanOnVirtualThread);
        } finally {
            executor.shutdownNow();
        }
    }
    @TestTemplate
    void shouldHandleExecutorNamingConsistently(ThreadType threadType) throws Exception {
        log.info("Running shouldHandleExecutorNamingConsistently in {}", threadType);

        ScheduledExecutorService executor =
                ExecutorServiceFactory.newSingleThreadScheduledExecutor("executor-naming-test-" + threadType);

        Future<String> threadName = executor.submit(() -> Thread.currentThread().getName());

        try {
            String actualThreadName = threadName.get(1, TimeUnit.SECONDS);

            Assertions.assertThat(actualThreadName)
                    .as("Thread name should include the provided prefix in %s", threadType)
                    .containsIgnoringCase("executor-naming-test-" + threadType);

            log.info("Executor naming test passed in {} - Thread name: {}", threadType, actualThreadName);
        } finally {
            executor.shutdownNow();
        }
    }
}
