package jasper.basic.collections.map;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * @author jasper
 * @since 2026-04-23 15:23:06
 */
@Slf4j
public class CommonMethod {
    public static void main(String[] args) {
        HashMap<String, String> map = HashMap.newHashMap(30);
        map.put("test", "1");
        final String number = map.getOrDefault("test", "2");
        log.info("number = {}", number);
        final String number1 = map.getOrDefault("test1", "2");
        log.info("number1 = {}", number1);

        String s = map.computeIfAbsent("test", k -> "3");
        log.info("s = {}", s);
        String s1 = map.computeIfAbsent("test2", k -> "3");
        log.info("s1 = {}", s1);

        String s2 = map.computeIfPresent("test", (k, v) -> v + 1);
        log.info("s2 = {}", s2); // 1+1 = 11 str

        HashMap<String, Integer> counts = new HashMap<>();
        counts.merge("java",1,Integer::sum);
        counts.merge("java",1,Integer::sum);
        counts.merge("spring",1,Integer::sum);
        log.info("map={}",counts);


    }
}
