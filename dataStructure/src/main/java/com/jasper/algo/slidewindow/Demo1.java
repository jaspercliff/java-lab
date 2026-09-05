package com.jasper.algo.slidewindow;

/**
 * @version 1.0
 * @Author jasper
 * @Date 2025-01-09
 * @Description 在数组中查找连续k个元素的最大值
 */
public class Demo1 {

    /**
     * 固定窗口大小   先加出第一个窗口的和currentSum  然后向右移动  左边减去一个currentSum - num[i-k]  右边加一个
     * 3   3-1  3-1+(-3)
     * @param nums 数组
     * @param k  连续k个元素
     * @return max value
     */
    public static int maxSubArraySum(int[] nums, int k) {
        int maxSum = 0;
        int currentSum = 0;
        for (int i = 0; i < k; i++) {
            currentSum += nums[i];
        }
        maxSum = currentSum;
        System.out.println(currentSum);
        for (int i = k; i < nums.length; i++) {
            currentSum = currentSum - nums[i - k] + nums[i];
            maxSum = Math.max(maxSum, currentSum);
            System.out.println(currentSum);  // 3 -1 1 5 14 16
        }
        return maxSum;
    }

    public static void main(String[] args) {
        int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
        int k = 3;
        int maxSum = maxSubArraySum(nums, k);
        System.out.println("连续 " + k + " 个元素的最大和为：" + maxSum); // 输出：16
    }
}
