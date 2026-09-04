package jasper.juc.tools.cyclicbarrier;

import java.util.concurrent.CyclicBarrier;

/**
 * @author jasper
 * @since 2026-05-25 12:23:39 <br>
 *     允许一组线程互相等待， 直到所有线程都达到一个公共屏障点（Common Barrier Point）再继续执行 <br>
 *     主要线程协作 例如4个线程各自计算一部分 最后汇总
 */
public class CyclicBarrierDemo {
    public static void main(String[] args) {
        CyclicBarrier barrier =
                new CyclicBarrier(3, () -> System.out.println("all task  done 1 phase"));

        new Thread(new Task("A", barrier)).start();
        new Thread(new Task("B", barrier)).start();
        new Thread(new Task("C", barrier)).start();
    }
}
