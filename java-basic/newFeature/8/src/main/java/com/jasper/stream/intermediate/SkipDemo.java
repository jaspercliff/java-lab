package com.jasper.stream.intermediate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author jasper @Date 2024-11-11
 *
 * @version 1.0
 */
public class SkipDemo {
  public static void main(String[] args) {
    List<Integer> list = Arrays.asList(1, 1, 2, 3, 4, 5, 6);
    List<Integer> collect = list.stream().skip(2).collect(Collectors.toList());
    System.out.println("collector = " + collect);


    List<Integer> list1 = Arrays.asList(1, 1, 2, 3, 4, 5, 6);
    int pageNum = 2;
    int pageSize = 3;
    List<Integer> collect1 = list1.stream().skip((pageNum - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
    System.out.println("第二页的数据为" + collect1);
//    asd
    for(int i = 0; i < 10; i++) {
      System.out.println("as");
    }
  }


}
