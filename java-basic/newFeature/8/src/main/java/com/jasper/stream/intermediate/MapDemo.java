package com.jasper.stream.intermediate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class MapDemo {
    public static void main(String[] args) {
        // 将流中的元素转换为另外一种形式 接受一个Function,应用到每个元素
        final List<String> list = Arrays.asList("1", "2", "3");
        final Stream<Integer> integerStream = list.stream().map(Integer::parseInt);
        integerStream.forEach(System.out::println);
    }
}
