package com.jasper.optional;

import java.util.HashMap;
import java.util.Optional;

/**
 * @Author jasper @Date 2024-12-26
 *
 * @version 1.0 @Description
 */
public class OptionalDemo {
  public static void main(String[] args) {
    HashMap<String, Object> resultMap = new HashMap<>();
    resultMap.put("test", 1);
    Object test = Optional.ofNullable(resultMap.get("test")).orElse(0);
    Object test1 = Optional.ofNullable(resultMap.get("test1")).orElse(0);
    System.out.println(test);
    System.out.println(test1);
  }
}