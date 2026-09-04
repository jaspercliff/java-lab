package jasper.juc.collections.queue.arrayBlockingQueue;

import java.util.concurrent.ArrayBlockingQueue;

public class PutAndTakeDemo {
    public static void main(String[] args) {
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(3);

        new Thread(
                        () -> {
                            for (Integer i = 0; i < 3; i++) {
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
