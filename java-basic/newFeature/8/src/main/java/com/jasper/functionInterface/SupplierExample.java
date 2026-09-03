package com.jasper.functionInterface;

import java.util.function.Supplier;
public class SupplierExample {
    public static void main(String[] args) {
// 示例1：生成一个随机数
        Supplier<Double> randomNumberSupplier = () -> Math.random();
        System.out.println("Random Number: " + randomNumberSupplier.get());

        // 示例2：生成一个固定值
        Supplier<String> helloSupplier = () -> "Hello, World!";
        System.out.println("Message: " + helloSupplier.get());

        // 示例3：懒初始化（懒加载）
        Supplier<String> lazyInitializationSupplier = new Supplier<String>() {
            private String value;

            @Override
            public String get() {
                if (value == null) {
                    value = "This is a lazily initialized value.";
                }
                return value;
            }
        };

        System.out.println("Lazy Initialization: " + lazyInitializationSupplier.get());
    }
}