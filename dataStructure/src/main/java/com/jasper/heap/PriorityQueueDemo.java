package com.jasper.heap;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * 小顶堆：任意一个节点的值<=其子节点的值 <br>
 * 大顶堆：任意一个节点的值>=其子节点的值
 */
@Slf4j
public class PriorityQueueDemo {
    public static void main(String[] args) {
        final PriorityQueue<Integer> minHeap = new PriorityQueue<>(Arrays.asList(1, 3, 2, 4, 5));
        final PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);
        maxHeap.offer(1);
        maxHeap.offer(3);
        maxHeap.offer(2);
        maxHeap.offer(5);
        maxHeap.offer(4);


        log.info("min peek :{} " ,minHeap.peek());
        log.info("max peek :{} " ,maxHeap.peek());

        log.info("min size: {}",minHeap.size());
        log.info("max size: {}", maxHeap.size());

        while (!maxHeap.isEmpty()) {
            final Integer poll = maxHeap.poll();
            log.info("maxHeap poll : {}", poll);
        }
        while (!minHeap.isEmpty()) {
            final Integer poll = minHeap.poll();
            log.info("minHeap poll : {}", poll);
        }
    }
}
