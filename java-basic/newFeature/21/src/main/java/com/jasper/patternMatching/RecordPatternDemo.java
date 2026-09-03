package com.jasper.patternMatching;

// 定义 Record
record Point(int x, int y) {}
record Rectangle(Point topLeft, Point bottomRight) {}

public class RecordPatternDemo {

    // 而 JDK 21 正式支持对 record 进行解构（Deconstruction）。
    // 可以在模式匹配时，直接提取 record 内部的组件值，而无需手动调用 getter 方法
    static void analyzeShape(Object shape) {
        switch (shape) {
            // 1. 单层解构：匹配 Point 并提取 x, y
            case Point(int x, int y) -> 
                System.out.println("这是一个点，坐标: (" + x + ", " + y + ")");
                
            // 2. 嵌套解构：匹配 Rectangle，并直接提取内部 Point 的坐标
            case Rectangle(Point(int x1, int y1), Point(int x2, int y2)) -> 
                System.out.println("矩形，左上角(" + x1 + "," + y1 + ") 到 右下角(" + x2 + "," + y2 + ")");
                
            // 3. 嵌套解构 + 守卫模式 (when)
            case Rectangle(Point p1, Point p2) when p1.x() == p2.x() ->
                System.out.println("这是一条垂直线");
                
            default -> System.out.println("未知形状");
        }
    }

    public static void main(String[] args) {
        analyzeShape(new Point(10, 20)); 
        // 输出: 这是一个点，坐标: (10, 20)
        
        analyzeShape(new Rectangle(new Point(0, 0), new Point(100, 50))); 
        // 输出: 矩形，左上角(0,0) 到 右下角(100,50)
    }
}