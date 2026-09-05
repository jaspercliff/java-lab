package com.jasper.creational.signle;

import java.util.function.Supplier;

public class LazySingleSupplier {
    //   第一次请求时才实例化
    private static final Supplier<LazySingleSupplier> supplier = LazySingleSupplier::new;
    private LazySingleSupplier() {}
    public static LazySingleSupplier getInstance() {
        return supplier.get();
    }
}
