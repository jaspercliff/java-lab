package com.jasper.algo.lru.LinkedHashMapDemo;

import java.util.LinkedHashMap;

public class LruCache {
    private final LinkedHashMap<Integer, Integer> cache;

    public LruCache(int capacity) {
        //true 每次get put都会将该节点移动到尾部 最近使用
        //false 按照插入顺序
        cache = new LinkedHashMap<>(capacity,0.75f,true){
            /**
             * put后自动调用
             */
            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<Integer, Integer> eldest) {
                return size() > capacity;
            }
        };
    }

    public int get(int key) {
        return cache.getOrDefault(key, -1);
    }

    public void put(int key, int value) {
        cache.put(key, value);
    }

    public void printCache() {
        // 这里的顺序是：Head (最旧) -> Tail (最新)
        System.out.println("当前缓存顺序 (左旧 -> 右新): " + cache.keySet());
    }
}
