package com.jasper.stream;

import java.util.stream.IntStream;

public class IntStreamDemo {
    public static void main(String[] args) {
        //1-4
        IntStream.range(1,5).forEach(System.out::print);
        System.out.println();
        //1-5
        IntStream.rangeClosed(1,5).forEach(System.out::print);
        System.out.println();

        int[] numbers = {2, 4, 6, 8, 10};
        int sum = IntStream.of(numbers).sum();
        System.out.println("sum = " + sum);
        double average = IntStream.of(numbers).average().orElse(0);
        System.out.println("average = " + average);
        int max = IntStream.of(numbers).max().getAsInt();
        System.out.println("max = " + max);
    }
}
