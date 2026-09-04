package jasper.juc.lock.reentrantlock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 当锁释放的一瞬间，如果正好有一个新产生的线程尝试获取锁，它会直接和队列头部的线程竞争，而不会老老实实去排队 <br>
 * 叫号 必须先叫先占锁
 */
public class fairDemo {

    public static void main(String[] args) throws InterruptedException {
        // 修改此处的 true/false 来切换公平与非公平模式
        ReentrantLock lock = new ReentrantLock(true);
        // 线程任务：获取锁后立即释放，并重复多次
        Runnable task =
                () -> {
                    for (int i = 0; i < 2; i++) {
                        lock.lock();
                        try {
                            System.out.println(Thread.currentThread().getName() + " 获取到了锁");
                            // 模拟持有锁的时间，增加竞争概率
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            lock.unlock();
                        }
                    }
                };

        // 启动多个线程
        for (int i = 0; i < 5; i++) {
            new Thread(task, "线程-" + i).start();
        }
    }
}
