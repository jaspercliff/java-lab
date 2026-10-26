package com.jasper.structural.decorator;

public class MilkDecorator extends CoffeeDecorator {

    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public double cost() {
        return coffee.cost() + 3.0;
    }

    @Override
    public String description() {
        return coffee.description() + " + 牛奶";
    }
}