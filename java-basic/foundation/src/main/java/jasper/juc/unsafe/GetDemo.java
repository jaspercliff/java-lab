package jasper.juc.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author jasper
 * @since 2026-04-29 22:04:40
 */
@SuppressWarnings("removal")
public class GetDemo {
    public static void main(String[] args)
            throws NoSuchFieldException,
                    SecurityException,
                    IllegalArgumentException,
                    IllegalAccessException {
        // theUnsafe 的私有静态成员变量，它持有单例对象
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);

        User user = new User();

        Field field = User.class.getDeclaredField("age");
        // 这里虽然没有实例化 但是类加载的时候会在类元信息记录偏移地址 没有分配 只是记录偏移量
        long offset = unsafe.objectFieldOffset(field);
        System.out.println("offset = " + offset);
        unsafe.putInt(user, offset, 100);
        System.out.println(user);
    }
}
