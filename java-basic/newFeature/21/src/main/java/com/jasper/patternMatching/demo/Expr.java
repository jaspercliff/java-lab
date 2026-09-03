package com.jasper.patternMatching.demo;

// 1. 定义封闭接口，限定只有特定的几个实现类
public sealed interface Expr permits ConstantExpr, AddExpr, MultiplyExpr {}
