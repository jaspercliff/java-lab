package com.jasper.algo.sort;

/**
 * 建立大顶堆 → 每次把堆顶最大值放到数组末尾 → 缩小堆 → 重新调整堆
 */
public class HeapSort {

    public static void sort(int[] arr) {
        // 6 1 3 4 2
//        左孩子 = 2 * i + 1
//        右孩子 = 2 * i + 2
//        父节点 = (i - 1) / 2
        // 数字按完全二叉树映射
        int n = arr.length;

        // 1. 建立大顶堆
        // 只有非叶子节点需要进行下沉 n/2-1 最后一个非叶子节点
        for (int i = n / 2 - 1; i >= 0; i--) {
            siftDown(arr, i, n);
        }

        // 2. 不断把最大值放到数组末尾
        for (int end = n - 1; end > 0; end--) {
            swap(arr, 0, end);

            // 重新调整剩余部分
            siftDown(arr, 0, end);
        }
    }

    /**
     * 将 arr[root] 向下调整
     * size 表示当前堆的大小
     */
    private static void siftDown(int[] arr, int root, int size) {
        while (true) {
            int left = root * 2 + 1;

            // 没有左孩子
            if (left >= size) {
                break;
            }

            int right = left + 1;

            // 选择较大的孩子
            int child = left;

            if (right < size && arr[right] > arr[left]) {
                child = right;
            }

            // 父节点已经 >= 最大孩子
            if (arr[root] >= arr[child]) {
                break;
            }

            // 交换
            swap(arr, root, child);

            root = child;
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}