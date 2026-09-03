package com.jasper.stream.terminal;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @Author jasper @Date 2024-11-13
 *
 * @version 1.0
 */
public class ReduceDemo {
        public static void main(String[] args) {
            List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);

            Integer reduce = list.stream().reduce(3, Integer::sum);
            System.out.println("reduce = " + reduce);

            Optional<Integer> reduce1 = list.stream().reduce((a, b) -> a * b);
            System.out.println("reduce1 = " + reduce1);

            Integer reduce2 = list.stream().reduce(3, (a, b) -> a * b);
            System.out.println("reduce2 = " + reduce2);
        }
}
