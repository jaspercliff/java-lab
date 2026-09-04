package jasper.jvm.gc.reference;

import java.lang.ref.WeakReference;

/**
 * @author jasper
 * @since 2026-05-25 <br>
 */
public class WeakRefDemo {
    // 只要发生gc 就回收
    public static void main(String[] args) {
        Object strongObj = new Object();
        WeakReference<Object> weakRef = new WeakReference<>(strongObj);

        System.out.println("GC前: " + weakRef.get()); // 结果：对象地址

        strongObj = null; // 切断强引用，现在只有弱引用指向它
        System.gc(); // 触发 GC

        System.out.println("GC后: " + weakRef.get()); // 结果：null
    }
}
