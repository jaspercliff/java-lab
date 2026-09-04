package jasper.juc.lock.reentrantlock;

import java.util.concurrent.locks.ReentrantLock;

/** synchronized 可重入是jvm维护的monitor reentrantLock 是aqs 实现的 */
public class ReentrantDemo {
    ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        ReentrantDemo service = new ReentrantDemo();
        service.a();
    }

    public void a() {
        lock.lock();
        try {
            System.out.println("outter");
            b();
        } finally {
            lock.unlock();
        }
    }

    public void b() {
        lock.lock(); // 重入 不会死锁
        try {
            System.out.println("inner");
        } finally {
            lock.unlock();
        }
    }
}
