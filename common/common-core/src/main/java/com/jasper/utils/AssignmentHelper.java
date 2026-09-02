package com.jasper.utils;

import com.jasper.lang.ObjectUtils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 属性赋值与对比辅助工具类
 *
 * @author Jasper
 * @since 1.0.0
 */
public final class AssignmentHelper {

    private AssignmentHelper() {}

    /** 【精简版】空值安全赋值 抛弃掉繁琐的 targetObject 穿透，直接利用普通 Consumer，调用起来爽快一倍 */
    public static <T> void setIfNull(
            Supplier<T> valueSupplier, Consumer<T> setterConsumer, T defaultValue) {
        T value = valueSupplier.get();
        if (ObjectUtils.isNull(value)) {
            setterConsumer.accept(defaultValue);
        }
    }

    /** 【进化版】维护前后不相等的字段值（全类型通配） 将原本的 String 泛型化为 <V>，无论是 Integer, Boolean 还是 String 全能比对 */
    public static <U, V> void setIfChanged(
            U targetObject,
            BiConsumer<U, V> setterConsumer,
            Supplier<V> beforeValueSupplier,
            Supplier<V> afterValueSupplier) {

        V beforeValue = beforeValueSupplier.get();
        V afterValue = afterValueSupplier.get();

        // 只有新旧值不相等时，才触发赋值
        if (ObjectUtils.isNotEquals(beforeValue, afterValue)) {
            setterConsumer.accept(targetObject, afterValue);
        }
    }
}
