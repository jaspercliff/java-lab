package com.jasper.thread;

import java.util.HashMap;
import java.util.Map;

/**
 * ThreadLocalManager - 基于ThreadLocal的线程安全参数管理器
 * 使用HashMap存储键值对，生命周期与线程绑定
 */
public class ThreadLocalManager {

    // 使用ThreadLocal来持有每个线程独立的HashMap
    private static final ThreadLocal<Map<String, Object>> threadLocal = ThreadLocal.withInitial(HashMap::new);

    /**
     * 设置参数
     * @param key 参数名
     * @param value 参数值
     */
    public static void set(String key, Object value) {
        threadLocal.get().put(key, value);
    }

    /**
     * 获取参数
     * @param key 参数名
     * @return 参数值，如果不存在则返回null
     */
    public static Object get(String key) {
        return threadLocal.get().get(key);
    }

    /**
     * 获取参数并转换为指定类型
     * @param key 参数名
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 转换后的对象，如果不存在或类型不匹配则返回null
     *
     * clazz.isInstance 如果是null则返回false
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key, Class<T> clazz) {
        Object value = get(key);
        if (clazz.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    /**
     * 检查是否包含某个key
     * @param key 参数名
     * @return 是否存在
     */
    public static boolean contains(String key) {
        return threadLocal.get().containsKey(key);
    }

    /**
     * 移除某个参数
     *
     * @param key 参数名
     */
    public static void remove(String key) {
        threadLocal.get().remove(key);
    }

    /**
     * 获取当前线程所有参数的不可变快照（只读副本）
     * @return 参数快照
     */
    public static Map<String, Object> snapshot() {
        return Map.copyOf(threadLocal.get());
    }

    /**
     * 清空当前线程的所有参数
     * map.clear() 清除当前map的所有键值对
     */
    public static void clear() {
        threadLocal.get().clear();
    }

    /**
     * threadLocal 为每一个线程维护了一个变量副本 当线程生命周期结束之后会自动释放该副本 <br/>
     * 但是线程池中的线程任务结束之后不会被销毁，所以threadLocal也不会被回收，如果不显式清除，就会一直占用内存 <br/>
     * 手动清理当前线程的ThreadLocal资源
     * 建议在使用线程池的场景下，在任务结束前调用此方法，防止内存泄漏
     */
    public static void remove() {
        threadLocal.remove();
    }

}