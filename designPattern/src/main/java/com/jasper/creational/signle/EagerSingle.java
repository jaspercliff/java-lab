package com.jasper.creational.signle;

import lombok.Getter;

/**
 * 饿汉式
 * 没有加锁 效率好一点
 * 一开始类就初始化了 如果没有使用造成了内存浪费
 */
public class EagerSingle {
    @Getter
    private static final EagerSingle instance = new EagerSingle();
    private EagerSingle() {
    }

}
