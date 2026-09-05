package com.jasper.str;

/**
 * @author jasper
 * @since 2026-05-06 10:21:21
 */
public class KMP {

    private int[] buildNext(String pattern) {
        int n = pattern.length();
        int[] next = new int[n];
        int j = 0;
        for (int i = 1; i < n; i++) {
            while (j > 0 && pattern.charAt(i) != pattern.charAt(j)) {
                // j is the length of the matched prefix
                // next[j-1]还能复用的最长前缀
                j = next[j - 1];
            }
            if (pattern.charAt(i) == pattern.charAt(j)) {
                j++;
            }
            next[i] = j;
        }
        return next;
    }

    public int strStr(String str, String needle) {
        if (needle.isEmpty()) return 0;
        int[] next = buildNext(needle);
        int j = 0;
        for (int i = 0; i < str.length(); i++) {
            while (j > 0 && str.charAt(i) != needle.charAt(j)) {
                j = next[j - 1]; // j is the length of the matched prefix
            }
            if (str.charAt(i) == needle.charAt(j)) {
                j++;
            }
            if (j == needle.length()) {
                return i - needle.length() + 1;
            }
        }
        // if not return -1
        return -1;
    }
}
