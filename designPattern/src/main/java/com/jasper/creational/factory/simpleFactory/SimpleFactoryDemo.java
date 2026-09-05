package com.jasper.creational.factory.simpleFactory;

public class SimpleFactoryDemo {
    public static void main(String[] args) {
        Shape circle = ShapeFactory.getShape("CIRCLE");
        assert circle != null;
        circle.draw();
        Shape square = ShapeFactory.getShape("SQUARE");
        assert square != null;
        square.draw();
    }
}
