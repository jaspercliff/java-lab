package com.jasper.algo.lru.nomap;

public class Node {
    public int key,value;
    public Node prev,next;

    public Node(int key,int value){
        this.key = key;
        this.value = value;
    }
}
