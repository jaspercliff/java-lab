package com.jasper.stack;

import java.util.Deque;
import java.util.LinkedList;

@Deprecated
public class LinkedListDemo {
    public static void main(String[] args) {
        Deque<Integer> stack = new LinkedList<>();
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
