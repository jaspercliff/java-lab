package jasper.basic.reflect;

import com.jasper.constants.ThreadType;

public class EnumReflect {
    public static void main(String[] args) {
        ThreadType[] enumConstants = ThreadType.class.getEnumConstants();
        for (ThreadType t : enumConstants) {
            System.out.println(t);
        }
    }
}
