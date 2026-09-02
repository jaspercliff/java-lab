package com.jasper.functional;

/**
 * 允许抛出受检异常的数据消费者接口。
 *
 * <p>该接口是 {@link java.util.function.Consumer} 的增强版本，接收一个参数且无返回值。
 * 常用于遍历集合、流式处理（forEach）中需要对元素进行打印、持久化或远程发送等可能报错的场景。
 *
 * @param <T> 被消费的数据类型
 * @author Jasper
 * @since 1.0.0
 */
@FunctionalInterface
public interface CheckedConsumer<T> {

    /**
     * 消费/处理给定的数据。
     *
     * @param t 输入的数据对象
     * @throws Exception 允许抛出任何受检或运行时异常
     */
    void accept(T t) throws Exception;
}
