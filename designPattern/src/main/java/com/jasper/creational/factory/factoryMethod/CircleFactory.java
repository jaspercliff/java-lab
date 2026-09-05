package com.jasper.creational.factory.factoryMethod;

import com.jasper.creational.factory.simpleFactory.Circle;
import com.jasper.creational.factory.simpleFactory.Shape;

public class CircleFactory implements ShapeFactory {

    @Override
    public Shape createShape() {
        return new Circle();
    }
}
