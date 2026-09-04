package jasper.basic.dataType.enums;

public class MainDemo {
    public static void main(String[] args) {
        for (ColorEnum enum1 : ColorEnum.values()) {
            System.out.println(enum1);
        }
        System.out.println(ColorEnum.RED.name());
        // ordinal 返回枚举常量的“下标（序号）”
        System.out.println(ColorEnum.RED.ordinal());
        // valueOf 返回指定字符串的枚举常量 字符串 → 枚举
        System.out.println(ColorEnum.valueOf("GREEN"));
    }
}
