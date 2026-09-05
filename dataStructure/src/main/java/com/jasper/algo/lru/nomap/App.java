package com.jasper.algo.lru.nomap;

public class App {
    public static void main(String[] args) {
        LruCacheC lru = new LruCacheC(3);
        lru.put(1, 1);
        lru.put(2, 2);
        lru.put(3, 3); // 1 2 3
        lru.print();
        System.out.println(lru.get(1)); //2 3 1
        lru.print();
        lru.put(4, 4);// 2 淘汰 3 1 4
        lru.print();
        System.out.println(lru.get(2));
        lru.put(3, 30);// 1 4 3
        lru.print();
    }
}
