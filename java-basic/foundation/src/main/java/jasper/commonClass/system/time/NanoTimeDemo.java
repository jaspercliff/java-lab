package jasper.commonClass.system.time;

import java.util.concurrent.TimeUnit;

/**
 * @author jasper
 * @since 2026-07-15 23:29:44 <br>
 *     用来测量时间间隔（计算耗时）的黄金标准 调用操作系统的底层高精度计时器来实现的 <br>
 *     即时改了系统时间也不影响该时间
 */
public class NanoTimeDemo {
    public static void main(String[] args) throws InterruptedException {
        // 1s = 1000ms = 1000 um = 1000ns
        long start = System.nanoTime();
        Thread.sleep(1000);
        long end = System.nanoTime();
        long millis = TimeUnit.NANOSECONDS.toMillis(end - start);
        System.out.println("millis = " + millis);
    }
}
