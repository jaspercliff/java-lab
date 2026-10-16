package jasper.juc.future;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class CompletableFutureCommonMethod {
    public static void main(String[] args) {
//        创建一个已经执行完成、并且已经有结果的 CompletableFuture
//        任务不用执行了，直接把结果包装成 CompletableFuture 返回
        CompletableFuture<String> future = CompletableFuture.completedFuture("Hello");
        log.info(String.valueOf(future.isDone()));
        log.info(future.join());
    }
}
