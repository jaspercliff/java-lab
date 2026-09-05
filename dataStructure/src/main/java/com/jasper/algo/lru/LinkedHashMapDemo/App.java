package com.jasper.algo.lru.LinkedHashMapDemo;

public class App {
    public static void main(String[] args) {
        LruCache lru = new LruCache(3);
        lru.put(1, 1);
        lru.put(2, 2);
        lru.put(3, 3); // 1 2 3
        lru.printCache();
        System.out.println(lru.get(1)); //2 3 1
        lru.printCache();
        lru.put(4, 4);// 2 淘汰 3 1 4
        lru.printCache();
        System.out.println(lru.get(2));
        lru.put(3, 30);// 1 4 3
        lru.printCache();
    }
}
