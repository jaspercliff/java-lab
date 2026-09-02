package com.jasper.functional;

/**
 * 允许抛出受检异常的数据提供者（结果生产者）接口。
 * <p>
 * 该接口是 {@link java.util.function.Supplier} 的增强版本，允许在获取数据时向外抛出异常。
 * 常用于需要获取执行结果且可能发生 I/O、数据库等异常的场景。
 * </p>
 *
 * @param <T> 期望返回的数据类型
 * @author Jasper
 * @since 1.0.0
 */
@FunctionalInterface
public interface CheckedSupplier<T> {

    /**
     * 获取执行结果。
     *
     * @return 生产的数据对象
     * @throws Exception 允许抛出任何受检或运行时异常
     */
    T get() throws Exception;
}
