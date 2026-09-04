package jasper.basic.dataType.enums;

public class EnumDemo {
    public static void main(String[] args) {
        enum Day {
            SUNDAY, MONDAY, TUESDAY, WEDNESDAY,
            THURSDAY, FRIDAY, SATURDAY
        }

        for (Day day : Day.values()) {
//            枚举常量在类中声明的顺序 从0开始
            System.out.println(day + " has ordinal: " + day.ordinal());
            System.out.println(day.name());
        }
    }
}
