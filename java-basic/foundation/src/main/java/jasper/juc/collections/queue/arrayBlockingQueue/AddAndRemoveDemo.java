package jasper.juc.collections.queue.arrayBlockingQueue;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 内存预分配，创建时直接分配好数组空间，运行期间没有节点创建和回收的开销，对 GC 非常友好。
 * 因为只有一把锁，在极高并发下，锁竞争会比 LBQ 激烈，吞吐量上限略低于 LBQ。
 * 容量固定，无法动态扩容
 */
public class AddAndRemoveDemo {
    public static void main(String[] args) {
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(3);
        // queue.remove(); // Exception in thread "main" java.util.NoSuchElementException
        boolean isSuccess =
                queue.remove(3); // Deleting the specified element will not throw an exception
        // if not return false
        System.out.println(isSuccess);
        queue.add(1);
        queue.add(2);
        queue.add(3);
        // queue.add(4); // Exception in thread "main" java.lang.IllegalStateException: Queue full
    }
}
