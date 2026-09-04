package jasper.basic.collections.queue.deque.ArrayDeque;

import java.util.ArrayDeque;
import java.util.Deque;

public class Queue2 {
    public static void main(String[] args) {

        Deque<Integer> queue = new ArrayDeque<>();
        queue.addLast(1);
        queue.addLast(2);
        queue.addLast(3);
        while (!queue.isEmpty()) {
            Integer removeFirst = queue.removeFirst();
            System.out.println(removeFirst); // 1 2 3
        }
    }
}
