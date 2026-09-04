package jasper.juc.lock;

public class TestReadWriteLock {
    public static void main(String[] args) {
        ReadWriteLockExample example = new ReadWriteLockExample();


        // 启动一个写线程
        new Thread(() -> {
            example.write(42);
        }, "写线程").start();

        // 启动多个读线程
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                System.out.println("读取结果：" + example.read());
            }, "读线程-" + i).start();
        }


    }
}