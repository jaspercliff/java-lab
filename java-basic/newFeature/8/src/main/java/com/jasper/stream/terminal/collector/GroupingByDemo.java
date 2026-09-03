package com.jasper.stream.terminal.collector;


import com.jasper.pojo.entity.Cup;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author jasper @Date 2024-11-14
 *
 * @version 1.0
 */
public class GroupingByDemo {
  public static void main(String[] args) {
    List<Cup> list = Arrays.asList(
            new Cup("hh", 1),
            new Cup("bb", 1),
            new Cup("gg", 1),
            new Cup("cc", 2)
    );
    Map<Integer, List<Cup>> collect = list.stream().collect(Collectors.groupingBy(Cup::getHigh));
    System.out.println("collect = " + collect);

    Map<Integer, Long> collect1 = list.stream().collect(Collectors.groupingBy(Cup::getHigh, Collectors.counting()));
    System.out.println("collect1 = " + collect1);

    Map<Integer, IntSummaryStatistics> collect2 = list.stream()
            .collect(Collectors.groupingBy(Cup::getHigh, Collectors.summarizingInt(Cup::getHigh)));
    System.out.println("collect2 = " + collect2);
  }
}
