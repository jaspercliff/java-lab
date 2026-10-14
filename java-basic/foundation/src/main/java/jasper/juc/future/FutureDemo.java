package jasper.juc.future;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class FutureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Integer> future = executorService.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                return 42;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        log.info("submit complete");

//        阻塞获取
        Integer i = future.get();
        log.info("i={}",i);
        executorService.shutdown();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future1= executor.submit(() -> {
            for (int j = 0; j < 10; j++) {
                System.out.println(j);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    log.info("线程被中断，退出任务");
                    return;
                }
            }
        });

        TimeUnit.SECONDS.sleep(1);
        future1.cancel(true);
        executor.shutdown();
    }
}
