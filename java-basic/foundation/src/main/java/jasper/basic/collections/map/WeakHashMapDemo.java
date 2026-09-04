package jasper.basic.collections.map;

import java.util.Map;
import java.util.WeakHashMap;

public class WeakHashMapDemo {

    public static void main(String[] args) throws Exception {

        Map<Object, String> map = new WeakHashMap<>();

        Object key = new Object();

        map.put(key, "hello");

        System.out.println("GC前: " + map);

        key = null;

        System.gc();

        Thread.sleep(1000);

        System.out.println("GC后: " + map);
    }
}
