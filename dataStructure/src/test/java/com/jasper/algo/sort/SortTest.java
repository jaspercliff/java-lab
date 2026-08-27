package com.jasper.algo.sort;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

@Slf4j
public class SortTest {

    private static void info(int[] arr) {
        log.info("arr : {}", Arrays.toString(arr));
    }

    @Test
    public void testHeapSort() {
        int[] arr = new int[]{6, 1, 3, 4, 2};
        HeapSort.sort(arr);
        info(arr);
    }


    @Test
    public void testMergeSort() {
        int[] arr = new int[]{6, 1, 3, 4, 2};
        MergeSort.sort(arr);
        info(arr);
    }
}
