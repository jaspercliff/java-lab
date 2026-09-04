package jasper.basic.collections.map.newFeat;

import java.util.HashMap;

/**
 * @author jasper
 * @since 2026-04-23 14:48:10
 */
public class Demo {
    public static void main(String[] args) {
        //  会自己计算符合100大小的map 而不会到75就扩容 since 19
        HashMap<String, String> map = HashMap.newHashMap(100);
        map.put("1", "apple");
        map.put("2", "orange");
    }
}
