package com.jasper.stack;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 比stack和linkedlist更快 <br>
 * 线程不安全
 */
public class ArrayDequeDemo {
    public static void main(String[] args) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(1);
        stack.push(2);
        Integer peek = stack.peek();
        System.out.println("peek = " + peek);
        while (!stack.isEmpty()) {
            Integer o = stack.pop();
            System.out.println(o);
        }
    }
}
