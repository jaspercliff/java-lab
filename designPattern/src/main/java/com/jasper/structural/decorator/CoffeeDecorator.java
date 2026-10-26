package com.jasper.structural.decorator;

public abstract class CoffeeDecorator implements Coffee {
//    装饰器内部持有一个 Coffee 对象，可以是原始咖啡，也可以是另一个装饰器
    protected final Coffee coffee;

    protected CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
}