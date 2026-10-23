package com.jasper.functionInterface;

import lombok.extern.slf4j.Slf4j;

//BiPredicate 可以接收两个输入参数，并进行逻辑判断，最终返回一个布尔值
@Slf4j
public class BiPredicateDemo {
    public static void main(String[] args) {
        java.util.function.BiPredicate<Integer, Integer> isSumGreaterThanTen = (a, b) -> (a + b) > 10;

        log.info("isSumGreaterThanTen.test(5, 6): {}", isSumGreaterThanTen.test(5, 6)); // true
        log.info("isSumGreaterThanTen.test(3, 4): {}", isSumGreaterThanTen.test(3, 4)); // false
    }
}
