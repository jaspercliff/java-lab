package com.jasper.taskExecutionAndScheduling.taskExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jspecify.annotations.NonNull;
import org.springframework.core.task.TaskDecorator;

/**
 * Spring 提供的一个任务装饰器接口，主要用于在任务执行前后添加额外逻辑
 * 在线程池执行异步任务时，传递 ThreadLocal 上下文，例如 MDC、TraceId、用户上下文等
 */
public class LoggingTaskDecorator implements TaskDecorator {

	private static final Log logger = LogFactory.getLog(LoggingTaskDecorator.class);

	@Override
	public Runnable decorate(@NonNull Runnable runnable) {
		return () -> {
			logger.info("Before execution of " + runnable);
			runnable.run();
			logger.info("After execution of " + runnable);
		};
	}
}