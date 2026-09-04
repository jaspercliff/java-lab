package jasper.juc.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author jasper
 * @since 2026-04-30 15:07:48
 */
@SuppressWarnings("removal")
public class MemoryManage {
    public static void main(String[] args)
            throws NoSuchFieldException,
                    SecurityException,
                    IllegalArgumentException,
                    IllegalAccessException {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        long address = unsafe.allocateMemory(8);
        System.out.println("address = " + address);

        unsafe.putLong(address, 123456L);
        long long1 = unsafe.getLong(address);
        System.out.println("long1 = " + long1);

        unsafe.freeMemory(address);
    }
}
