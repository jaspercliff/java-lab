package jasper.basic.collections.map;

import java.util.HashMap;

/**
 * @author jasper
 * @since 2026-04-23 15:23:06
 */
public class CommonMethod {
    public static void main(String[] args) {
        HashMap<String, String> map = HashMap.newHashMap(30);
        map.put("test", "1");
        final String number = map.getOrDefault("test", "2");
        System.out.println("number = " + number);
        final String number1 = map.getOrDefault("test1", "2");
        System.out.println("number1 = " + number1);
    }
}
