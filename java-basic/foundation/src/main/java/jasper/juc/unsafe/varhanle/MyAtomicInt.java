package jasper.juc.unsafe.varhanle;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;

/**
 * @author jasper
 * @since 2026-04-30 14:50:45
 */
public class MyAtomicInt {
    private volatile int value;

    private static final VarHandle VALUE_HANDLE;

    static {
        Lookup lookup = MethodHandles.lookup();
        try {
            VALUE_HANDLE = lookup.findVarHandle(MyAtomicInt.class, "value", int.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public int manualIncrementAndGet() {
        int prev;
        do {
            // 相当于 getIntVolatile
            prev = (int) VALUE_HANDLE.getVolatile(this);
        } while (!VALUE_HANDLE.compareAndSet(this, prev, prev + 1));
        return prev + 1;
    }
}
