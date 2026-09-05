package com.jasper.queue;

import java.util.ArrayDeque;
import java.util.Deque;

/** queue impl */
public class ArrayDequeDemo {
    public static void main(String[] args) {
        Deque<Integer> queue = new ArrayDeque<>();
        queue.offer(1);
        queue.offer(2);
        queue.offer(3);
        Integer peek = queue.peek();
        System.out.println("peek: " + peek);
        while (!queue.isEmpty()) {
            Integer poll = queue.poll();
            System.out.println(poll); // 1 2 3
        }
    }
}
