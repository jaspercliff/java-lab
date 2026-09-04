package jasper.juc.atomic;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jasper
 * @since 2026-04-30 11:54:37
 */
public class AtomicIntegerDemo {
    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        System.out.println(atomicInteger.get());
        atomicInteger.set(1);
        System.out.println(atomicInteger.get());

        // 获取旧值并设置新值
        int oldValue = atomicInteger.getAndSet(2);
        System.out.println(
                "get and set oldValue:" + oldValue + ", newValue:" + atomicInteger.get());
        // 获取旧值并自增
        int oldValue1 = atomicInteger.getAndIncrement();
        System.out.println("getAndIncrement:" + oldValue1 + ", newValue:" + atomicInteger.get());
        // 自增后返回新值
        int newValue = atomicInteger.incrementAndGet();
        System.out.println("getAndIncrement newValue:" + atomicInteger.get() + "-" + newValue);

        // 添加当前值并返回新值
        int addValue = atomicInteger.addAndGet(2);
        System.out.println("addAndGet , addValue:" + addValue);

        // 如果当前值等于预期值，则更新为给定的更新值
        boolean b = atomicInteger.compareAndSet(2, addValue);
        System.out.println("是否成功更新:" + b + ",atomicInteger.get():" + atomicInteger.get());

        boolean b1 = atomicInteger.compareAndSet(6, 4);
        System.out.println("是否成功更新:" + b1 + ",atomicInteger.get():" + atomicInteger.get());
    }
}
