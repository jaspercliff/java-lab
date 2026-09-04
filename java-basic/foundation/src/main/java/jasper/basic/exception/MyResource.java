package jasper.basic.exception;

/**
 * @author jasper
 * @since 2026-07-16 20:58:24 <br>
 *     AutoCloseable 适合 是其他通用的资源管理类 <br>
 *     void close() throws Exception;
 */
public class MyResource implements AutoCloseable {
    public void doSome() {
        System.out.println("do something");
    }

    @Override
    public void close() throws Exception {
        System.out.println("AutoCloseable");
    }
}
