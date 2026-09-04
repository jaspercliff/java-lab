package jasper.jvm.jit;

public class LockCoarseningDemo {
    private static final Object LOCK = new Object();
    private static int counter = 0;

    public static void main(String[] args) throws Exception {
        // 预热：让 JIT 编译器注意到这个方法，将其编译为机器码
        for (int i = 0; i < 20_000; i++) {
            doSomethingCombined();
        }

        // 正式测试
        long start = System.currentTimeMillis();
        for (int i = 0; i < 50_000_000; i++) {
            doSomethingCombined();
        }
        long end = System.currentTimeMillis();

        System.out.println("最终计数器: " + counter);
        System.out.println("耗时: " + (end - start) + " ms");
    }

    private static void doSomethingCombined() {
        // 【锁粗化前】：这里有 4 个独立的 synchronized 块
        // 【JVM 优化后】：JIT 会直接把这 4 个锁合并，只在最外面加一次锁、最后解一次锁
        synchronized (LOCK) {
            counter++;
        }
        synchronized (LOCK) {
            counter++;
        }
        synchronized (LOCK) {
            counter++;
        }
        synchronized (LOCK) {
            counter++;
        }
    }
}
