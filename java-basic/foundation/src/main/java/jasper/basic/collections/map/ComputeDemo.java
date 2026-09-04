package jasper.basic.collections.map;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jasper
 * @since 2026-05-10 14:42:38
 */
public class ComputeDemo {
    public static void main(String[] args) {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("Java", 10);

        // 使用 compute 更新值
        counts.compute("Java", (key, value) -> (value == null) ? 1 : value + 1);
        counts.compute("Rust", (key, value) -> (value == null) ? 1 : value + 1);

        System.out.println(counts); // 输出: {Java=11, Rust=1}
    }
}
