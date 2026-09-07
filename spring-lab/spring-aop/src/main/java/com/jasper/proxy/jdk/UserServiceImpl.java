package com.jasper.proxy.jdk;

// 2. 实现类（目标对象）
public class UserServiceImpl implements UserService {
    @Override
    public void addUser() {
        System.out.println("执行添加用户操作...");
    }
}