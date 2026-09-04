package jasper.juc.lock.reentrantlock;

import java.util.concurrent.locks.ReentrantLock;

public class interruptDemo {

    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Thread t1 =
                new Thread(
                        () -> {
                            lock.lock();
                            System.out.println("t1 get lock and long time occuption!");
                            try {
                                Thread.sleep(10000); // 10s
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                lock.unlock();
                            }
                        });
        Thread t2 =
                new Thread(
                        () -> {
                            try {
                                System.out.println("t2 get lock interruptibly");
                                lock.lockInterruptibly();
                                try {
                                    System.out.println("t2 get lock success");
                                } finally {
                                    lock.unlock();
                                }
                            } catch (InterruptedException e) {
                                System.out.println("t2 get lock fail : interruptibly");
                            }
                        });

        t1.start();
        Thread.sleep(100); // t1 get lock first
        t2.start();

        Thread.sleep(1000); // make sure t2 getting lock
        System.out.println("main thread interrupt");
        t2.interrupt();
    }
}

/*
t1 get lock and long time occuption!
t2 get lock interruptibly
main thread interrupt
t2 get lock fail : interruptibly
*/
