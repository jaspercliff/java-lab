package jasper.juc.collections.queue;

import java.util.concurrent.LinkedBlockingQueue;

public class LinkedBlockingQueueDemo {
    public static void main(String[] args) {
        //  default capacity: Integer.MAX_VALUE 也可以不指定
        LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>(3);
        new Thread(
                        () -> {
                            for (int i = 0; i < 3; i++) {
                                try {
                                    queue.put(i); // i = 3 will blocking until there is a consumer
                                } catch (InterruptedException e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                        })
                .start();

        new Thread(
                        () -> {
                            try {
                                Thread.sleep(2000);
                                while (true) {
                                    System.out.println(queue.take()); // blocking wait
                                }

                            } catch (InterruptedException e) {
                                System.out.println(e.getMessage());
                            }
                        })
                .start();
    }
}
