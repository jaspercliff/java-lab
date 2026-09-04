package jasper.juc.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockExample {
    private int value = 0;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * 多个读线程会同时获取读锁，不会互相阻塞。
     */
    public int read() {
        lock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 正在读取...");
            Thread.sleep(100); // 模拟耗时读取
            return value;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 写操作是独占锁，必须等待其他线程释放读锁，它自己才能加上写锁。
     */
    public void write(int newValue) {
        lock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 正在写入...");
            Thread.sleep(200); // 模拟耗时写入
            this.value = newValue;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println(Thread.currentThread().getName() + " 写入完成...");
            lock.writeLock().unlock();
        }
    }
}