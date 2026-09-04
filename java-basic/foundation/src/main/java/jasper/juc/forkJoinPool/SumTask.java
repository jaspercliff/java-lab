package jasper.juc.forkJoinPool;

import java.util.concurrent.RecursiveTask;

public class SumTask extends RecursiveTask<Long> {
    private final long start;
    private final long end;
    private static final long THRESHOLD = 10000;

    public SumTask(long start, long end) {
        this.start = start;
        this.end = end;
    }
    @Override
    protected Long compute() {
        if ((end - start) <= THRESHOLD) {
            long sum = 0;
            for (long i = start; i <= end; i++) {
                sum += i;
            }
            return sum;
        }
        long step = (end-start) / 4;
        long mid1 = start + step;
        long mid2 = start + step*2;
        long mid3 = start + step*3;

        final SumTask task1 = new SumTask(start, mid1);
        final SumTask task2 = new SumTask(mid1,mid2);
        final SumTask task3 = new SumTask(mid2,mid3);
        final SumTask task4 = new SumTask(mid3,end);
        task1.fork();//async
        task2.fork();
        task3.fork();
        final Long result4 = task4.compute();//当前线程同步
        final Long result1 = task1.join();
        final Long result2 = task2.join();
        final Long result3 = task3.join();
        return result1+result2+result3+result4;
    }
}
