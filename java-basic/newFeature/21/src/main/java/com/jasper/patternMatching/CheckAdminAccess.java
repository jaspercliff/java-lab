package com.jasper.patternMatching;

import java.util.List;

// 1. 定义 Record，用于承载数据
record User(String name, int age, List<String> roles) {}

public class CheckAdminAccess {

    public void checkAdminAccess(Object obj) {
        switch (obj) {
            // 分支 1: 匹配 User 类型，解构出 name, age, roles
            // 使用 when 守卫条件：年龄 >= 18 且 roles 列表中包含 "ADMIN"
            case User(String name, int age, List<String> roles)
                    when age >= 18 && roles.contains("ADMIN") ->
                    System.out.println("欢迎管理员: " + name);

            // 分支 2: JDK 21+ 未命名模式 (Unnamed Pattern)，使用 '_' 忽略不需要的字段 (roles)
            // 守卫条件：年龄 < 18
            case User(String name, int age, List<String> roles)
                    when age < 18 ->
                    System.out.println("用户 " + name + " 未成年，拒绝访问");

            // 分支 3: JDK 21+ 支持将 null 和 default 合并处理
            // 捕获: null 值、非 User 类型的对象、或者不满足上述条件的 User (如成年非管理员)
            case null, default ->
                    System.out.println("无效的用户对象或非管理员成年用户: " + obj);
        }
    }

    public static void main(String[] args) {
        CheckAdminAccess checker = new CheckAdminAccess();

        System.out.println("--- 开始测试 JDK 21 模式匹配 ---\n");

        // 测试用例 1: 成年管理员 (命中分支 1)
        User admin = new User("Alice", 25, List.of("USER", "ADMIN"));
        System.out.print("测试 1 (成年管理员): ");
        checker.checkAdminAccess(admin);

        // 测试用例 2: 未成年用户 (命中分支 2)
        User minor = new User("Bob", 16, List.of("USER"));
        System.out.print("测试 2 (未成年用户): ");
        checker.checkAdminAccess(minor);

        // 测试用例 3: 成年非管理员 (命中分支 3: default)
        User regularUser = new User("Charlie", 30, List.of("USER"));
        System.out.print("测试 3 (成年非管理员): ");
        checker.checkAdminAccess(regularUser);

        // 测试用例 4: null 对象 (命中分支 3: null)
        System.out.print("测试 4 (null 对象): ");
        checker.checkAdminAccess(null);

        // 测试用例 5: 非 User 类型的对象 (命中分支 3: default)
        System.out.print("测试 5 (非 User 对象): ");
        checker.checkAdminAccess("这是一个字符串，不是 User");
    }
}