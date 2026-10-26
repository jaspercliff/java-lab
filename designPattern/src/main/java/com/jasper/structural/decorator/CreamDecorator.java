package com.jasper.structural.decorator;

public class CreamDecorator extends CoffeeDecorator {

    public CreamDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public double cost() {
        return coffee.cost() + 5.0;
    }

    @Override
    public String description() {
        return coffee.description() + " + 奶油";
    }
}