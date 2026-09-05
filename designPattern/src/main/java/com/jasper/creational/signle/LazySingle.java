package com.jasper.creational.signle;

/**
 * 懒汉式
 * 使用到的时候才会初始化
 * 会有多线程并发问题
 */
public class LazySingle {
    private static LazySingle instance;

    private LazySingle() {
    }

    public static LazySingle getInstance() {
        if (instance == null) {
            instance = new LazySingle();
        }
        return instance;
    }
}
