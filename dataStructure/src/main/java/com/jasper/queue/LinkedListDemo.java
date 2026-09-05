package com.jasper.queue;

import java.util.Deque;
import java.util.LinkedList;

@Deprecated
public class LinkedListDemo {
    public static void main(String[] args) {

        Deque<Integer> queue = new LinkedList<>();
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
