package jasper.juc.tools.countdownlatch.rocketmq;

import java.util.concurrent.TimeUnit;

public class CountDownLatch2Demo {
    public static void main(String[] args) throws InterruptedException {
        final CountDownLatch2 countDownLatch2 = new CountDownLatch2(3);

        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                System.out.println("子线程开始执行: " + Thread.currentThread().getName());
                countDownLatch2.countDown();
            }).start();
        }

        final boolean complete = countDownLatch2.await(5, TimeUnit.SECONDS);
        if (complete){
            System.out.println("所有子线程执行完毕，主线程继续执行");
        } else {
            System.out.println("等待超时，主线程继续执行");
            return;
        }
        countDownLatch2.reset();
        System.out.println("CountDownLatch2 重置后，当前计数为: " + countDownLatch2.getCount());

        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                System.out.println("子线程开始重复执行: " + Thread.currentThread().getName());
                countDownLatch2.countDown();
            }).start();
        }
    }
}
