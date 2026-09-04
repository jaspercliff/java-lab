package jasper.juc.collections.queue.arrayBlockingQueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class OfferAndPollDemo {

    public static void main(String[] args) throws InterruptedException {
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(1);
        Integer poll = queue.poll();
        System.out.println(poll); // return null
        queue.offer(1);
        Integer poll2 = queue.poll();
        System.out.println(poll2);
        Integer poll3 = queue.poll(2, TimeUnit.SECONDS); // with timeout
        System.out.println(poll3);

        queue.offer(2);
        boolean offer = queue.offer(3);
        System.out.println("offfer success: " + offer);
    }
}
