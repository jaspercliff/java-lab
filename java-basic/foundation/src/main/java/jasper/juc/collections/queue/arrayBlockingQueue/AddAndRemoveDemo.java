package jasper.juc.collections.queue.arrayBlockingQueue;

import java.util.concurrent.ArrayBlockingQueue;

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
