package jasper.juc.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author jasper
 * @since 2026-04-30 11:51:24
 */
@SuppressWarnings({"removal", "unused"})
class MyAtomicInt {
    private volatile int value;

    private static final Unsafe unsafe;
    private static final long offset;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);

            offset = unsafe.objectFieldOffset(MyAtomicInt.class.getDeclaredField("value"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int incrementAndGet() {
        int prev;
        do {
            prev = unsafe.getIntVolatile(this, offset);
        } while (!unsafe.compareAndSwapInt(this, offset, prev, prev + 1));
        return prev + 1;
    }
}
