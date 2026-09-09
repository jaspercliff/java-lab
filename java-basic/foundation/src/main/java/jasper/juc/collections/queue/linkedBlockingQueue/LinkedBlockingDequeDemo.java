package jasper.juc.collections.queue.linkedBlockingQueue;

import java.util.concurrent.LinkedBlockingDeque;

/** 双端队列
 * 并发机制：两把锁（putLock 用于入队，takeLock 用于出队）。
 * 特点：
 * 读写分离，并发度高。
 * 每次入队都需要 new Node()，出队后节点会被 GC 回收，有一定的内存分配和 GC 压力
 */
public class LinkedBlockingDequeDemo {
    public static void main(String[] args) throws InterruptedException {
        // also 默认Interger.maxvalue 容易oom
        LinkedBlockingDeque<Integer> qDeque = new LinkedBlockingDeque<Integer>(100);
        qDeque.putFirst(1);
        qDeque.putFirst(2);
        qDeque.putLast(3); // 2 1 3
        qDeque.putFirst(4);
        qDeque.putLast(5); // 4 2 1 3 5
        Integer takeFirst = qDeque.takeFirst(); // 2 1 3 5
        System.out.println(takeFirst);
        Integer takeLast = qDeque.takeLast(); // 2 1 3
        System.out.println(takeLast);
        while (!qDeque.isEmpty()) {
            Integer take = qDeque.take();
            System.out.println(take);
        }
    }
}
