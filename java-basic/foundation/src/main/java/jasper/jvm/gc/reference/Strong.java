package jasper.jvm.gc.reference;

/**
 * @author jasper
 * @since 2026-05-25 16:24:30
 */
public class Strong {
    // 强引用一直不会回收 除非手动断开 eg：obj=null；
    public static void main(String[] args) {
        Object obj = new Object(); // 强引用
        System.gc(); // 建议jvm进行gc
        System.out.println(obj); // 仍然存在
    }
}
