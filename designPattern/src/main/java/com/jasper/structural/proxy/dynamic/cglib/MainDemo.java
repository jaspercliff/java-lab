package com.jasper.structural.proxy.dynamic.cglib;

import net.sf.cglib.proxy.Enhancer;

/**
 * CGLIB 为了生成子类，必须调用 ClassLoader.defineClass。在 Java 8 之前，这是合法的；
 * 但在 Java 17 中，java.base 模块默认对外部“封闭”了这些底层接口
 * 添加vm option:  --add-opens java.base/java.lang=ALL-UNNAMED
 */
public class MainDemo {
    static void main() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(AwsSmsService.class);
        // proxy enhance
        enhancer.setCallback(new WatchInterceptor());
        AwsSmsService proxy = (AwsSmsService)enhancer.create();
        proxy.send("110","hello");
        proxy.send("120","world");
    }
}
