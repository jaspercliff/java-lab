package com.jasper.patternMatching;

public class SwitchPatternDemo {
    
    static String formatObject(Object obj) {
        return switch (obj) {
            case null -> "它是 null"; // 1. 直接处理 null
//            匹配类型的同时，直接声明并初始化变量
            case String s -> "字符串，长度: " + s.length(); // 2. 类型匹配并绑定变量 s
            case Integer i when i > 0 -> "正整数: " + i; // 3. 守卫模式 (when)
            case Integer i -> "非正整数: " + i;
            case Double d -> "浮点数: " + d;
            default -> "未知类型: " + obj.getClass().getSimpleName();
        };
    }

    public static void main(String[] args) {
        System.out.println(formatObject("Hello")); // 输出: 字符串，长度: 5
        System.out.println(formatObject(42));      // 输出: 正整数: 42
        System.out.println(formatObject(-5));      // 输出: 非正整数: -5
        System.out.println(formatObject(null));    // 输出: 它是 null
    }
}