package jasper.juc.threadlocal;

/**
 * @author jasper
 * @since 2026-05-25 21:51:42 <br>
 */
public class InheritableThreadLocalDemo {
    public static void main(String[] args) {
        InheritableThreadLocal<String> local = new InheritableThreadLocal<>();
        local.set("hello");
        // 创建子线程时父线程 ThreadLocal 会拷贝一份
        new Thread(
                        () -> {
                            System.out.println(local.get());
                        })
                .start();
    }
}
