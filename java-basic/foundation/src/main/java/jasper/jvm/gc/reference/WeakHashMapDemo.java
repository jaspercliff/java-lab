package jasper.jvm.gc.reference;

import java.util.Map;
import java.util.WeakHashMap;

public class WeakHashMapDemo {

    // 它的键（Key）是弱引用。只要它的某个 Key 在外界没有被任何强引用关联了
    // 那么在下一次 GC 时，这个 Key 对应的那一整条键值对（Key-Value）就会被自动从 Map中被清理干净
    public static void main(String[] args) throws Exception {

        Map<Object, String> map = new WeakHashMap<>();

        // key 必须是new出来的 string(字符串常量池) 和包装类（缓冲池）不行
        Object key = new Object();

        map.put(key, "hello");

        System.out.println("GC前: " + map);

        key = null;

        System.gc();

        Thread.sleep(1000);

        System.out.println("GC后: " + map);
    }
}
