package com.jasper.creational.signle;

public class DoubleCheckSingle {
    /**
     * volatile 保证顺序     确保初始化在 instance被引用之前。 防止指令重新排序
     */
    private static volatile DoubleCheckSingle instance;

    private DoubleCheckSingle() {
    }

    /**
     * 这是因为当一个线程A正在同步块内执行实例化操作时，其他线程（比如线程B）会被阻塞在外，直到线程A完成实例化并退出同步块。
     * 此时，如果没有第二次检查，线程B直接跳过判断并重新创建一个新的实例，就会导致多个实例被创建的问题。
     * 因此，第二次判空确保了即便在多线程环境下，也只会创建一个实例
     * @return instance
     */
    public static DoubleCheckSingle getInstance() {
        if (instance == null) {
            synchronized (DoubleCheckSingle.class) {
                if (instance == null) {
                    instance = new DoubleCheckSingle();
                }
            }
        }
        return instance;
    }
}
