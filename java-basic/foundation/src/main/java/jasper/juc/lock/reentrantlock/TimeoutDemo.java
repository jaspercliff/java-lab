package jasper.juc.lock.reentrantlock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class TimeoutDemo {
    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();

        Thread t1 =
                new Thread(
                        () -> {
                            lock.lock();
                            try {
                                System.out.println("thread a is running");
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                System.out.println(e.getMessage());
                            } finally {
                                lock.unlock();
                            }
                        });

        Thread t2 =
                new Thread(
                        () -> {
                            boolean isSuccess = false;
                            try {
                                isSuccess = lock.tryLock(2, TimeUnit.SECONDS);
                                if (isSuccess) {
                                    System.out.println("thread b is running");
                                } else {
                                    System.out.println("thread b trylock fail timeout");
                                }
                            } catch (InterruptedException e) {
                                System.out.println(e.getMessage());
                            } finally {
                                if (isSuccess) lock.unlock();
                            }
                        });
        t1.start();
        Thread.sleep(500); // make sure t1 gets lock first
        t2.start();
    }
}
