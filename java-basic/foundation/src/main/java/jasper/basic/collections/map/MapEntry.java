package jasper.basic.collections.map;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author jasper
 * @since 2026-04-23 15:39:34
 */
public class MapEntry {
    public static void main(String[] args) {
        HashMap<Integer, Integer> map = HashMap.newHashMap(10);
        map.put(1, 1);
        map.put(2, 2);
        map.put(3, 3);
        Set<Entry<Integer, Integer>> entrySet = map.entrySet();
        for (Entry<Integer, Integer> entry : entrySet) {
            System.out.println(
                    "entry.key: " + entry.getKey() + " entry.value: " + entry.getValue());
        }

        for (Integer key : map.keySet()) {
            System.out.println(key);
        }

        for (Integer value : map.values()) {
            System.out.println(value);
        }

        map.forEach((k, v) -> System.out.println(k + "->" + v));
    }
}
