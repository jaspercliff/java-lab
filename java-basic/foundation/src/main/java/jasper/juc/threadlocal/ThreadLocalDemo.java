package jasper.juc.threadlocal;

/**
 * @author jasper
 * @since 2026-05-25 <br>
 */
public class ThreadLocalDemo {
    // 创建一个 ThreadLocal 变量，并提供初始值
    private static final ThreadLocal<Integer> threadLocalValue = ThreadLocal.withInitial(() -> 1);

    public static void main(String[] args) {
        // 线程 A
        new Thread(
                        () -> {
                            threadLocalValue.set(10);
                            System.out.println("Thread A: " + threadLocalValue.get());
                            threadLocalValue.remove();
                        })
                .start();

        // 线程 B
        new Thread(
                        () -> {
                            try {
                                Thread.sleep(100); // 为了演示，确保线程 A 先执行
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            System.out.println("Thread B: " + threadLocalValue.get());
                            threadLocalValue.remove();
                        })
                .start();
    }
}
