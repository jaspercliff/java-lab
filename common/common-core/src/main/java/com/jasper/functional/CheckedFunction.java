package com.jasper.functional;

/**
 * 允许抛出受检异常的数据转换（映射）函数接口。
 * <p>
 * 该接口是 {@link java.util.function.Function} 的增强版本，接收一个输入参数并返回一个处理结果。
 * 常用于 Stream 流的 {@code map()} 操作中，对数据进行清洗、跨类型转换且期间可能抛出异常的场景。
 * </p>
 *
 * @param <T> 输入参数的类型
 * @param <R> 返回结果的类型
 * @author Jasper
 * @since 1.0.0
 */
@FunctionalInterface
public interface CheckedFunction<T, R> {

    /**
     * 将输入数据转换为另一种形式或结果。
     *
     * @param t 输入的数据对象
     * @return 转换后的结果对象
     * @throws Exception 允许抛出任何受检或运行时异常
     */
    R apply(T t) throws Exception;
}
