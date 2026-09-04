package jasper.jvm.classload.initialization.passiveReference;

/**
 * 常量在编译阶段会存入调用类的常量池中，本质上已经脱离了原有的类。因此，调用该常量时，不需要初始化定义常量的类
 */
class ConstClass {
    static { System.out.println("ConstClass init!"); }
    public static final String HELLO_WORLD = "hello world";
}

public class Test {
    public static void main(String[] args) {
        // 不会输出 ConstClass init!
        System.out.println(ConstClass.HELLO_WORLD);
    }
}