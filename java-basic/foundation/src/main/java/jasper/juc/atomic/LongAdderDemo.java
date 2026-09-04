package jasper.juc.atomic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author jasper
 * @since 2026-05-12 16:38:38 <br>
 *     1. **分散热点：** `LongAdder` 内部维护了一个 `base` 变量和一个 `Cell[]` 数组。 <br>
 *     2. **无竞争时：** 线程直接累加到 `base` 变量上。 <br>
 *     3. **有竞争时：** 不同的线程会被映射到 `Cell[]` 数组的不同索引位置，各自累加自己的 `Cell` 值。 <br>
 *     4. **最后求和：** 当你需要获取最终的总和时，调用 `sum()` 方法，它会将 `base` 和所有 `Cell` 里的值加起来返回。
 */
public class LongAdderDemo {
    public static void main(String[] args) throws InterruptedException {
        LongAdder count = new LongAdder();
        ExecutorService executors = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executors.submit(
                    () -> {
                        for (int j = 0; j < 1000; j++) {
                            count.increment();
                        }
                    });
        }
        executors.shutdown();
        executors.awaitTermination(1, TimeUnit.SECONDS);
        // final sum result
        System.out.println(count.sum());
    }
}
