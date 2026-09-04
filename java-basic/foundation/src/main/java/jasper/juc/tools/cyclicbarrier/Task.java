package jasper.juc.tools.cyclicbarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author jasper
 * @since 2026-05-25 12:23:45
 */
public class Task implements Runnable {

    private String name;

    private CyclicBarrier barrier;

    public Task(String name, CyclicBarrier barrier) {
        this.name = name;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        System.out.println(name + " phase 1 is currently underway");
        try {
            Thread.sleep((long) (Math.random() * 1000));
            System.out.println(name + " reach barrier");
            barrier.await(); // 所有线程都到达barrier之后才继续执行 can reuse
            System.out.println(name + " phase 2 is currently underway");
            Thread.sleep((long) (Math.random() * 1000));
            System.out.println(name + " reach barrier");
            barrier.await();
            System.out.println(name + "end");
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } catch (BrokenBarrierException e) {
            System.out.println(e.getMessage());
        }
    }
}
