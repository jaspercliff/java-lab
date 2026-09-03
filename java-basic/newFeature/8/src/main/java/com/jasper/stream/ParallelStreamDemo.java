package com.jasper.stream;

import com.jasper.monitor.TimeTracker;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author jasper
 * @since 2026-09-02 <br>
 */
@Slf4j
public class ParallelStreamDemo {
    public static void main(String[] args) {
        // 生成 1000 万个整数的列表
        List<Integer> numbers =
                IntStream.rangeClosed(1, 10_000_000).boxed().toList();

        TimeTracker.measure("串行流耗时",()->{
            long sum = numbers.stream().mapToLong(ParallelStreamDemo::expensiveOperation).sum();
            info(sum);
        });

        TimeTracker.measure("并行流处理",()->{
            long sum = numbers.parallelStream().mapToLong(ParallelStreamDemo::expensiveOperation).sum();
            info(sum);
        });
    }

    private static void info(long sum) {
        log.info("sum = {}", sum);
    }

    // 模拟一个耗时的 CPU 密集型计算任务
    private static long expensiveOperation(int number) {
        long result = 0;
        for (int i = 0; i < 50; i++) {
            result += ((long) number * i) % 7;
        }
        return result;
    }
}
