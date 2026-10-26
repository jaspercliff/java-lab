package com.jasper.structural.decorator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DecoratorDemo {

    public static void main(String[] args) {

        Coffee coffee = new SimpleCoffee();

        // 加牛奶
        coffee = new MilkDecorator(coffee);

        // 再加奶油
        coffee = new CreamDecorator(coffee);

        log.info("{}", coffee.description());
        log.info("{}", coffee.cost());

    }
}