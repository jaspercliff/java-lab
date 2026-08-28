package com.jasper.algo.merge;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

@Slf4j
public class KWayMerge {

    public static List<Integer> kWayMerge(List<List<Integer>> shards) {
        record Node(
                int value,
                int shardIndex, // 第几个list
                int index) {}
        PriorityQueue<Node> queue =
                new PriorityQueue<>(Comparator.comparingInt(node -> node.value));
        // 每个list中的第一个
        for (int i = 0; i < shards.size(); i++) {
            List<Integer> integers = shards.get(i);
            if (!integers.isEmpty()) {
                queue.offer(new Node(integers.getFirst(), i, 0));
            }
        }
        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            Node poll = queue.poll(); // 堆中取出最小的
            result.add(poll.value);

            int shardIndex = poll.shardIndex;
            int index = poll.index + 1;

            List<Integer> shard = shards.get(shardIndex);
            if (index < shard.size()) {
                // 添加下一个元素和堆顶元素比较
                queue.offer(new Node(shard.get(index), shardIndex, index));
            }
        }
        return result;
    }

    public static void main(String[] args) {
        List<List<Integer>> lists = List.of(List.of(1, 4, 7), List.of(2, 3, 8), List.of(5, 6, 9));

        List<Integer> result = kWayMerge(lists);
        log.info("result:{}", result);
    }
}
