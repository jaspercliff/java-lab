package com.jasper.behavioral.templateMethod;

public class Tea extends Beverage{

    private final boolean like;

    public Tea(boolean like){
        this.like = like;
    }
    @Override
    protected void brew() {
        System.out.println("用沸水冲泡茶叶");
    }

    @Override
    protected void addCondiments() {
        System.out.println("加入柠檬");
    }

    @Override
    protected boolean customAddCondiments() {
        return like;
    }

}
