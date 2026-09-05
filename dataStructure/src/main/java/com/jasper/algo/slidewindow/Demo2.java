package com.jasper.algo.slidewindow;

/**
 * @version 1.0
 * @Author jasper
 * @Date 2025-01-09
 * @Description 给定一个正整数数组 nums 和一个正整数 target，找到 nums 中和为 target 的最短连续子数组，并返回其长度。如果不存在这样的子数组，则返回 0。
 */
public class Demo2 {


    /**
     * 可变窗口大小 从左往右一直加   大于target了  左边一直减 减到小于target了 右边再加   length = right-left+1
     * @param nums
     * @param target
     * @return
     */
    public static int minSubArrayLen(int[] nums,int target){
        int left = 0;
        int right = 0;
        int minLen = Integer.MAX_VALUE;
        int sum = 0;
        while (right < nums.length){
            sum += nums[right];
            while (sum>=target){
                sum -= nums[left];
                minLen = Math.min(minLen,right-left+1);
                left++;
            }
            right++;
        }
        return minLen == Integer.MAX_VALUE ? 0 : minLen;
    }
    public static void main(String[] args) {
        int[] nums1 = {2, 3, 1, 2, 4, 3};
        int target1 = 7;
        System.out.println("最短子数组长度：" + minSubArrayLen(nums1, target1)); // 输出：2

        int[] nums2 = {1, 4, 4};
        int target2 = 4;
        System.out.println("最短子数组长度：" + minSubArrayLen(nums2, target2)); // 输出：1

        int[] nums3 = {1,1,1,1,1,1};
        int target3 = 10;
        System.out.println("最短子数组长度：" + minSubArrayLen(nums3, target3)); // 输出：0
    }
}
