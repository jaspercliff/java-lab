package com.jasper.algo.lru.nomap;

import java.util.HashMap;

public class LruCacheC {
    private HashMap<Integer,Node> map;
    private DoubleList cache;
    private int cap;

    public LruCacheC(int cap){
        this.cap = cap;
        map = new HashMap<>();
        cache = new DoubleList();
    }
    public int get(int key){
        if (!map.containsKey(key)) return -1;
        makeRecently(key);
        return map.get(key).value;
    }

    public void put(int key,int val){
        if (map.containsKey(key)) {
            Node node = map.get(key);
            node.value = val;
            makeRecently(key);
            return;
        }
        if (cap ==cache.getSize()){
            removeLeastRecently();//删除最久未使用的
        }
        addRecently(key,val);
    }

    private void makeRecently(int key){
        Node node = map.get(key);
        cache.remove(node);//已有值 先删除在添加 不然链表结构会乱
        cache.addLast(node);
    }

    private void deleteKey(int key){
        Node node = map.get(key);
        cache.remove(node);
        map.remove(key);
    }
    private void addRecently(int key,int val){
        Node node = new Node(key, val);
        cache.addLast(node);
        map.put(key,node);
    }

    private void removeLeastRecently(){
        Node node = cache.removeFirst();
        map.remove(node.key);
    }

    public void print(){
        cache.print();
    }

}

