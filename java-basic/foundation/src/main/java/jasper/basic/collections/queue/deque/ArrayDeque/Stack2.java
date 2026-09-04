package jasper.basic.collections.queue.deque.ArrayDeque;

import java.util.ArrayDeque;
import java.util.Deque;

public class Stack2 {
    public static void main(String[] args) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.addFirst(1);
        stack.addFirst(2);
        stack.addFirst(3);
        while (!stack.isEmpty()) {
            Integer removeFirst = stack.removeFirst();
            System.out.println(removeFirst); // 3 2 1
        }
    }
}
