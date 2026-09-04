package jasper.basic.collections.map;

import java.util.EnumMap;

/**
 * @author jasper
 * @since 2026-04-23 15:25:00
 *     <p>内部使用数组实现，因为枚举的数量是有限的，所以效率高于hashMap <br>
 *     EnumMap 是一个专门为枚举类型设计的 Map 实现，按枚举定义的类的顺序存储键值对。 键不能为空
 */
public class EnumMapDemo {

    public enum Day {
        SUNDAY,
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY
    }

    public static void main(String[] args) {
        final EnumMap<Day, Integer> enumMap = new EnumMap<>(Day.class);
        enumMap.put(Day.SUNDAY, 1);
        enumMap.put(Day.MONDAY, 2);
        enumMap.put(Day.TUESDAY, 3);
        enumMap.put(Day.WEDNESDAY, 4);
        enumMap.put(Day.THURSDAY, 5);
        enumMap.put(Day.FRIDAY, 6);
        enumMap.put(Day.SATURDAY, 7);
        for (Day day : Day.values()) {
            System.out.println(day + " has value: " + enumMap.get(day));
        }
    }
}
