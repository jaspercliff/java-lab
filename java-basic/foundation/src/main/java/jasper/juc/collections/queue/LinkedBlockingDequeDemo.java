package jasper.juc.collections.queue;

import java.util.concurrent.LinkedBlockingDeque;

/** 双端队列 */
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
