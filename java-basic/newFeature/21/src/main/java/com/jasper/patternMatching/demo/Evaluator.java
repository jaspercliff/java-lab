package com.jasper.patternMatching.demo;

public class Evaluator {
    
    // 3. 使用 switch 模式匹配进行递归求值
    public static int evaluate(Expr expr) {
        return switch (expr) {
            // 解构 ConstantExpr
            case ConstantExpr(int val) -> val;
            
            // 嵌套解构 AddExpr，并递归调用 evaluate
            case AddExpr(Expr left, Expr right) -> evaluate(left) + evaluate(right);
            
            // 嵌套解构 MultiplyExpr
            case MultiplyExpr(Expr left, Expr right) -> evaluate(left) * evaluate(right);
            
            // 注意：这里不需要 default！
            // 因为 Expr 是 sealed 的，编译器知道上述 3 个 case 已经穷尽了所有可能。
            // 如果未来新增一个 SubtractExpr，编译器会在这里报错，提醒你修改 switch，极其安全。
        };
    }

    public static void main(String[] args) {
        // 构建表达式: (2 + 3) * 4
        Expr expr = new MultiplyExpr(
                new AddExpr(new ConstantExpr(2), new ConstantExpr(3)),
                new ConstantExpr(4)
        );
        
        System.out.println("结果: " + evaluate(expr)); // 输出: 结果: 20
    }
}
