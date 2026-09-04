package jasper.juc.lock.reentrantlock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class ConditionDemo {
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition notFull = lock.newCondition();
    private static final Condition notEmpty = lock.newCondition();
    private static int buffer = 0;
    private static final int CAPACITY = 10;

    public static void main(String[] args) {
        Thread producer =
                new Thread(
                        () -> {
                            for (int i = 0; i < 20; i++) {
                                produce();
                            }
                        });

        Thread consumer =
                new Thread(
                        () -> {
                            for (int i = 0; i < 20; i++) {
                                consume();
                            }
                        });

        producer.start();
        consumer.start();
    }

    static void produce() {
        lock.lock();
        try {
            while (buffer == CAPACITY) {
                try {
                    notFull.await(); // 等待 notFull 条件被满足
                } catch (InterruptedException e) {
                    log.info(e.getMessage());
                }
            }
            buffer++;
            System.out.println("Produced: " + buffer);
            notEmpty.signal(); // 唤醒等待的消费者
        } finally {
            lock.unlock();
        }
    }

    static void consume() {
        lock.lock();
        try {
            while (buffer == 0) {
                try {
                    notEmpty.await(); // 等待 notEmpty 条件被满足
                } catch (InterruptedException e) {
                    log.info(e.getMessage());
                }
            }
            buffer--;
            System.out.println("Consumed: " + buffer);
            notFull.signal(); // 唤醒等待的生产者
        } finally {
            lock.unlock();
        }
    }
}
