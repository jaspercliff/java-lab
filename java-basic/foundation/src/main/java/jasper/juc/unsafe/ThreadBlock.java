package jasper.juc.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author jasper
 * @since 2026-04-30 15:23:15 锁的底层
 */
@SuppressWarnings({"removal"})
public class ThreadBlock {
    public static void main(String[] args)
            throws NoSuchFieldException,
                    SecurityException,
                    IllegalArgumentException,
                    IllegalAccessException,
                    InterruptedException {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        Thread t =
                new Thread(
                        () -> {
                            System.out.println("start park");
                            // 如果 isAbsolute 是 false，0L 意味着无限期等待，直到被显式唤醒（unpark）或被中断
                            // 如果你写 1,000,000,000L（1秒，单位纳秒），线程会睡 1 秒后自动醒来
                            unsafe.park(false, 0L);
                            System.out.println("unparked");
                        });
        t.start();
        Thread.sleep(2000);
        unsafe.unpark(t);
    }
}
