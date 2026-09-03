package jasper.commonClass.str;

/**
 * @author jasper
 * @since 2026-05-05 10:18:42 <br>
 *     不支持索引替换 保证不可变性
 */
public class ReplaceDemo {
    public static void main(String[] args) {
        String a = "hello world";
        // 替换全部匹配的
        String replace = a.replace("world", "jasper");
        System.out.println("replace = " + replace);
        String email = "LeetCode@LeetCode.com";
        StringBuilder sb = new StringBuilder(email.toLowerCase());
        // 索引替换 sb
        sb.replace(1, 4, "abc"); // 1 - 3 替换为abc
        System.out.println(sb); // labccode@leetcode.com
        // 区间内全部改为 某个字符，自己循环
        int index = sb.indexOf("@");
        if (index != -1) {
            System.out.println("index = " + index); // 8
            for (int i = 1; i < index - 1; i++) {
                sb.setCharAt(i, '*');
            }
            System.out.println(sb);
        } else {
            System.out.println("not email");
        }
    }
}
