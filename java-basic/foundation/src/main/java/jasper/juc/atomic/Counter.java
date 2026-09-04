package jasper.juc.atomic;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jasper
 * @since 2026-04-30 12:51:48
 */
public class Counter {
    private AtomicInteger atomicCounter = new AtomicInteger(0);

    public void increment() {
        atomicCounter.incrementAndGet(); // 以原子方式将当前值加1
    }

    public int getCounter() {
        return atomicCounter.get(); // 获取当前值
    }

    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();
        Thread t1 =
                new Thread(
                        () -> {
                            for (int i = 0; i < 1000; i++) counter.increment();
                        });
        Thread t2 =
                new Thread(
                        () -> {
                            for (int i = 0; i < 1000; i++) counter.increment();
                        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Final count is: " + counter.getCounter()); // 2000
    }
}
