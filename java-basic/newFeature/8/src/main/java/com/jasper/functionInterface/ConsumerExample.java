package com.jasper.functionInterface;

import java.util.function.Consumer;

public class ConsumerExample {
    public static void main(String[] args) {
        Consumer<String> printUpperCase = str -> System.out.println(str.toUpperCase());
        Consumer<String> printLength = str -> System.out.println("Length: " + str.length());

        Consumer<String> combinedConsumer = printUpperCase.andThen(printLength);
        combinedConsumer.accept("hello world");
    }
}
