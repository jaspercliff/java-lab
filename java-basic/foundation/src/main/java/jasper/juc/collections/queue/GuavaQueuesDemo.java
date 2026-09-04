package jasper.juc.collections.queue;

import com.google.common.collect.Queues;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class GuavaQueuesDemo {
    public static void main(String[] args) throws InterruptedException {
        final ArrayBlockingQueue<String> data = new ArrayBlockingQueue<>(6);

        for (int i = 0; i < 5; i++) {
            data.offer("data-" + i);
        }

        final ArrayList<String> strings = new ArrayList<>();
        Queues.drain(data,strings,4,3, TimeUnit.SECONDS);
        System.out.println(strings);
    }
}
