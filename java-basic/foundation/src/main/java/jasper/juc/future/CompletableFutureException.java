package jasper.juc.future;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class CompletableFutureException {
    public static void main(String[] args) {
         CompletableFuture.supplyAsync(
                () -> {
                    throw new RuntimeException("boom");
                }
        ).exceptionally(ex->"fallback").thenAccept(System.out::println);

         CompletableFuture.supplyAsync(
                () -> {
                    if (true) {
                        throw new RuntimeException("boom");
                    }
                    return "res";
                }
         ).handle((res,ex)->{
             if (ex!=null){
                 log.error(String.valueOf(ex));
             }
             return res;
         }).thenAccept(System.out::println);

        //  类似于handle 但是不能改变结果 只能用于日志、清理等副作用
        CompletableFuture.supplyAsync(() -> "Hello")
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.err.println("Failed: " + ex);
                    } else {
                        System.out.println("Success: " + result);
                    }
                })
                .thenAccept(System.out::println);
    }


}
