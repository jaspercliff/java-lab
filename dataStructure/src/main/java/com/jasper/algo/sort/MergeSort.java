package com.jasper.algo.sort;

/**
 * 不断二分：把数组拆成左右两半，直到每部分只有一个元素。
 * 不断合并：把两个已经有序的数组合并成一个更大的有序数组
 */
public class MergeSort {
    public static void sort(int[] arr) {
        int[] temp = new int[arr.length];
        mergeSort(arr, temp, 0, arr.length - 1);
    }

    private static void mergeSort(int[] arr, int[] temp, int left, int right) {
        if (left >= right) {
            return;
        }

        int mid = left + (right - left) / 2;

        // 左半部分排序
        mergeSort(arr, temp, left, mid);

        // 右半部分排序
        mergeSort(arr, temp, mid + 1, right);

        // 合并两个有序区间
        merge(arr, temp, left, mid, right);
    }

    private static void merge(
            int[] arr,
            int[] temp,
            int left,
            int mid,
            int right
    ) {
        int i = left;
        int j = mid + 1;
        int k = left;

        // 两个区间比较
        //i 左半部分当前指针
        //j  右半部分当前指针
        //k  temp 当前要放的位置
        while (i <= mid && j <= right) {
            if (arr[i] <= arr[j]) {
                temp[k++] = arr[i++];
            } else {
                temp[k++] = arr[j++];
            }
        }

        // 左边剩余元素
        while (i <= mid) {
            temp[k++] = arr[i++];
        }

        // 右边剩余元素
        while (j <= right) {
            temp[k++] = arr[j++];
        }

        // 写回原数组
        if (right + 1 - left >= 0)
            System.arraycopy(temp, left, arr, left, right + 1 - left);
    }
}
