package jasper.basic.exception;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author jasper
 * @since 2026-07-16 21:00:16 <br>
 *     closable 适合 跟输入输出（I/O）相关的工具类 <br>
 *     public void close() throws IOException;
 */
public class MyResource1 implements Closeable {

    public void doSome() {
        System.out.println("do something");
    }

    @Override
    public void close() throws IOException {
        System.out.println("Closeable");
    }
}
