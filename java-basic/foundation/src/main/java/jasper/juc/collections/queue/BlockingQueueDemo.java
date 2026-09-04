package jasper.juc.collections.queue;

import java.util.concurrent.ArrayBlockingQueue;

public class BlockingQueueDemo {
    public static void main(String[] args) {

        final ArrayBlockingQueue<String> data = new ArrayBlockingQueue<>(3);

        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    data.put("data-" + i);
                    System.out.println("生产者-生产了: data-" + i);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    data.take();
                    System.out.println("消费者-消费了: data-" + i);
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
