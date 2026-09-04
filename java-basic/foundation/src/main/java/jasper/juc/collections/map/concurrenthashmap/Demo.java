package jasper.juc.collections.map.concurrenthashmap;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jasper
 * @since 2026-04-29 17:30:11
 */
public class Demo {
    public static void main(String[] args) {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
        map.put("1", "a");
        // Node 节点中的 val 和 next 指针都是由 volatile 修饰的 确保可见性和指令重排序
        System.out.println(map.get("1"));
    }
}
