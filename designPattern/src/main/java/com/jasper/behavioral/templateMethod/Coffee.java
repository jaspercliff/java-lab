package com.jasper.behavioral.templateMethod;

public class Coffee extends Beverage{
    @Override
    protected void brew() {
        System.out.println("用沸水冲泡咖啡粉");
    }

    @Override
    protected void addCondiments() {
        System.out.println("加入sugar和milk");
    }


}
