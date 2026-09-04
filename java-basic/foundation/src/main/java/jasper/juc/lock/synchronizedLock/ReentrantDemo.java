package jasper.juc.lock.synchronizedLock;

/** synchronized 可重入是jvm维护的monitor reentrantLock 是aqs 实现的 */
public class ReentrantDemo {
    public static void main(String[] args) {
        ReentrantDemo service = new ReentrantDemo();
        service.a();
    }

    /** code block and method 都是作用于对象 class and static method 作用于类 */
    public synchronized void a() {
        System.out.println("outter");
        b();
    }

    public synchronized void b() {
        System.out.println("inner");
    }
}
