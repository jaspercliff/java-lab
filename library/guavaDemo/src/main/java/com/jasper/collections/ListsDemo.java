package com.jasper.collections;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Author jasper
 * @Date 2024-09-05
 * @description Lists learn
 */
@Slf4j
public class ListsDemo {
    public static void main(String[] args) {
        List<Integer> list1 = Ints.asList(1, 2, 3, 4, 5);
        log.info("list1: {} " , list1);
        List<Integer> reverse = Lists.reverse(list1);
        log.info("reverse = {} " , reverse);
        Lists.partition(list1, 2).forEach(System.out::println);
//指定一个有初始容量的 arrayList
        ArrayList<String> list2 = Lists.newArrayListWithCapacity(3);
        list2.add("1");
        list2.add("2");
        list2.add("3");
        list2.add("4");
        log.info("list2 = " + list2);

        
    }
}
