package jasper.juc.threadlocal;

/**
 * @author jasper
 * @since 2026-05-25 21:52:39 <br>
 */
public class ThreadLocalSubThread {
    public static void main(String[] args) {
        ThreadLocal<String> threadLocal = new ThreadLocal<>();
        threadLocal.set("hello");
        new Thread(
                        () -> {
                            String x = threadLocal.get();
                            System.out.println("x = " + x);
                        })
                .start();
        ;
    }
}
