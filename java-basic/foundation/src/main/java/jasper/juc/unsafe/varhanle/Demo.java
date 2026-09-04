package jasper.juc.unsafe.varhanle;


import jasper.juc.unsafe.User;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

/**
 * @author jasper
 * @since 2026-04-29 23:16:09
 */
public class Demo {
    public static void main(String[] args) throws IllegalAccessException, NoSuchFieldException {
        // 1. 获取 LookUp 对象（这是获取 VarHandle 的工厂）
        // MethodHandles.lookup() 具有访问当前类的权限
        // 如果要访问其他类的私有字段，通常需要那个类授权或者使用 privateLookupIn
        // 相当于反射里的 f.setAccessible(true)
        MethodHandles.Lookup lookup =
                MethodHandles.privateLookupIn(User.class, MethodHandles.lookup());

        // 2. 获取 VarHandle（相当于 Unsafe + Offset 的结合体）
        // 参数：目标类、字段名、字段类型
        VarHandle ageHandle = lookup.findVarHandle(User.class, "age", int.class);

        // 3. 实例化对象
        User user = new User();
        System.out.println("Before: " + user);

        // 4. 执行写入（相当于 unsafe.putInt）
        // VarHandle 会自动关联到之前识别的那个“偏移量”
        ageHandle.set(user, 100);

        System.out.println("After: " + user);
    }
}
