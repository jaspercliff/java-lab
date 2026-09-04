package jasper.jvm.jmm;

import java.util.concurrent.CountDownLatch;

/**
 * @author jasper
 * @since 2026-05-21 17:24:08
 */
public class ReorderDemo {
    // only guarantees visilibity and reordering
    // not guarantees atomic
    public static volatile int count = 0;

    public static void incr() {
        count++;
    }

    public static void main(String[] args) throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(20);
        for (int i = 0; i < 20; i++) {
            new Thread(
                            () -> {
                                try {
                                    for (int j = 0; j < 100; j++) {
                                        incr();
                                    }
                                } finally {
                                    countDownLatch.countDown();
                                }
                            })
                    .start();
            ;
        }

        countDownLatch.await(); // 等待所有线程执行完
        System.out.println(count);
    }
}
