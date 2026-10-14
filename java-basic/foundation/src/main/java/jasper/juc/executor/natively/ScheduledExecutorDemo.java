package jasper.juc.executor.natively;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class ScheduledExecutorDemo {
    public static void main(String[] args) {
        // 创建一个核心线程数为 2 的定时线程池
        try (ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2)) {

            // 1. 延迟 3 秒后执行一次
            scheduler.schedule(() -> {
                System.out.println("延迟 3 秒执行的任务被触发，当前时间: " + System.currentTimeMillis());
            }, 3, TimeUnit.SECONDS);

            // 2. 初始延迟 1 秒后开始，每隔 2 秒执行一次 (fixedRate)   周期是从上一个任务开始的时间点开始计算的
            scheduler.scheduleAtFixedRate(() -> {
                System.out.println("FixedRate 任务执行中... Thread: " + Thread.currentThread().getName());
            }, 1, 2, TimeUnit.SECONDS);

            // 3. 初始延迟 1 秒后开始，上一次任务结束后延迟 3 秒再执行 (fixedDelay) 周期是从上一个任务完全结束的时间点开始计算 防止任务重叠并发执行
            scheduler.scheduleWithFixedDelay(() -> {
                System.out.println("FixedDelay 任务执行中...");
            }, 1, 3, TimeUnit.SECONDS);

            ScheduledFuture<Integer> future = scheduler.schedule(() -> {
                log.info("future demo");
                return 100;
            }, 3, TimeUnit.SECONDS);

            long delay = future.getDelay(TimeUnit.SECONDS);
            log.info("=======================future delay {}", delay);

            boolean done = future.isDone();
            log.info("=======================future done: {}", done);

            Thread.sleep(3000);
            if (!done) {
                future.cancel(true);
            }
            // 阻塞获取
            Integer o =  future.get();
            log.info("======================future o: {}", o);
        } catch (ExecutionException e) {
            log.info(e.getLocalizedMessage());
        } catch (InterruptedException e) {
            log.info(e.getMessage());
        }

        // scheduler.shutdown();
    }
}