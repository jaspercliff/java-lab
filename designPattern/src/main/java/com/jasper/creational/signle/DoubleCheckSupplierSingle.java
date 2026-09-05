package com.jasper.creational.signle;

import java.util.function.Supplier;

public class DoubleCheckSupplierSingle {
    // 使用 Supplier 来管理实例的创建
    private static final Supplier<DoubleCheckSupplierSingle> instanceSupplier = new Supplier<DoubleCheckSupplierSingle>() {
        private volatile DoubleCheckSupplierSingle instance;

        @Override
        public DoubleCheckSupplierSingle get() {
            if (instance == null) {
                synchronized (this) {
                    if (instance == null) {
                        instance = new DoubleCheckSupplierSingle();
                    }
                }
            }
            return instance;
        }
    };

    private DoubleCheckSupplierSingle() {
    }

    // 通过 Supplier 获取单例
    public static DoubleCheckSupplierSingle getInstance() {
        return instanceSupplier.get();
    }
}