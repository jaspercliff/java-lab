package jasper.juc.tools.countdownlatch;

import java.util.concurrent.CountDownLatch;

/**
 * @author jasper
 * @since 2026-05-25 11:18:14 <br>
 *     它允许一个或多个线程等待其他线程完成一系列操作
 */
public class CountdownlatchDemo {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(4);
        for (int i = 0; i < 3; i++) {
            new Thread(new Worker(i, latch)).start();
        }
        System.out.println("main thread");
        latch.countDown();
        latch.await(); // wait all thread done
        System.out.println("all thread is done");
    }
}
