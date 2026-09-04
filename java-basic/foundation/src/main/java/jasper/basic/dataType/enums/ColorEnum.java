package jasper.basic.dataType.enums;

public enum ColorEnum {

    // 枚举常量默认是 public static final 的
    // public static final com.jasper.dataType.enums.ColorEnum RED;
    // public static final com.jasper.dataType.enums.ColorEnum GREEN;
    // public static final com.jasper.dataType.enums.ColorEnum BLUE;
    RED("红色") {
        @Override
        public String getColor() {
            return "red";
        }
    },
    GREEN("绿色") {
        @Override
        public String getColor() {
            return "green";
        }
    },
    BLUE("蓝色") {
        @Override
        public String getColor() {
            return "blue";
        }
    };

    private String color;

    private ColorEnum(String color) {
        this.color = color;
    }

    // 抽象方法，所有枚举常量必须实现该方法
    public abstract String getColor();

}
