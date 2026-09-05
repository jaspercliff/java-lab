package com.jasper.creational.factory.factoryMethod;

import com.jasper.creational.factory.simpleFactory.Shape;
import com.jasper.creational.factory.simpleFactory.Square;

public class SquareFactory implements ShapeFactory{
    @Override
    public Shape createShape() {
        return new Square();
    }
}
