package jasper.juc.forkJoinPool;

import java.util.concurrent.ForkJoinPool;

public class ForkJoinPoolDemo {
    public static void main(String[] args) {
        try (ForkJoinPool forkJoinPool = new ForkJoinPool()) {
            final Long invoke = forkJoinPool.invoke(new SumTask(1, 100000));
            System.out.println("invoke = " + invoke);
        }
    }
}
