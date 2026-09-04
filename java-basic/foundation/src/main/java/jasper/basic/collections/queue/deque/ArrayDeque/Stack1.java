package jasper.basic.collections.queue.deque.ArrayDeque;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 比stack和linkedlist更快 <br>
 * 线程不安全
 */
public class Stack1 {
    public static void main(String[] args) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        Integer peek = stack.peek();
        System.out.println("peek = " + peek);
        while (!stack.isEmpty()) {
            Integer o = stack.pop();
            System.out.println(o); // 3 2 1
        }
    }
}
