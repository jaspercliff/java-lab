package com.jasper.creational.factory.simpleFactory;

/**
 *  根据提供的参数决定创建哪一个类
 *  新增产品时 需要修改产品工厂 违反了开闭原则
 */
public class ShapeFactory {
    public static Shape getShape(String shapeType){
        if(shapeType.equalsIgnoreCase("CIRCLE")){
            return new Circle();
        } else if(shapeType.equalsIgnoreCase("SQUARE")){
            return new Square();
        }
        return null;
    }
}
