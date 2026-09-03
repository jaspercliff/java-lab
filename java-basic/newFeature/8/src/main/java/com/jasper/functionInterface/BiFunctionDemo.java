package com.jasper.functionInterface;

import java.util.function.BiFunction;

public class BiFunctionDemo {
    public static void main(String[] args) {
        BiFunction<Integer,Integer,Integer> biFunction = Integer::sum;

        final Integer result = biFunction.apply(1, 2);
        System.out.println("result = " + result);
    }
}
