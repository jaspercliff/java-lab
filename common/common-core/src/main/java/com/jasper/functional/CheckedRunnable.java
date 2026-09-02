package com.jasper.functional;

/**
 * 允许抛出受检异常的无返回值任务接口。
 *
 * <p>该接口是 {@link java.lang.Runnable} 的增强版本，允许在执行时向外抛出任何类型的异常。<br>
 * 常用于耗时统计、失败重试等不需要返回结果的 Lambda 表达式块中。<br>
 * see also {@link com.jasper.monitor.TimeTracker}}
 *
 * @author Jasper
 * @since 1.0.0
 */
@FunctionalInterface
public interface CheckedRunnable {

    /**
     * 执行任务。
     *
     * @throws Exception 允许抛出任何受检或运行时异常
     */
    void run() throws Exception;
}
