package com.jasper.stack;

import java.util.Stack;

/**
 * 基于vector 线程安全 <br>
 * 但是性能一般 不推荐使用 recommand ArrayDeque
 */
@Deprecated
public class StackDemo {
    public static void main(String[] args) {
        Stack<Integer> stack = new Stack<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        if (!stack.isEmpty()) {
            Integer peek = stack.peek(); // watch and keep
            System.out.println(peek);
            int search = stack.search(peek); // return distance from stack peek : 1-based indexing
            System.out.println(search);
        }
        while (!stack.isEmpty()) {
            Integer pop = stack.pop();
            System.out.println(pop);
        }
    }
}
