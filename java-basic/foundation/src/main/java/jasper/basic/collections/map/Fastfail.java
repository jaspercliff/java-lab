package jasper.basic.collections.map;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author jasper
 * @since 2026-04-28 15:35:13
 */
public class Fastfail {
    public static void main(String[] args) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);

        Iterator<String> it = map.keySet().iterator();

        map.put("c", 3); // 修改了结构，modCount++

        it.next(); // 直接异常
    }
}
