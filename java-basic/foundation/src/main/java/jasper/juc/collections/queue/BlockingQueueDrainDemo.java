package jasper.juc.collections.queue;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class BlockingQueueDrainDemo {
    public static void main(String[] args) {
        final ArrayBlockingQueue<String> data = new ArrayBlockingQueue<>(10);

        for (int i = 0; i < 5; i++) {
            data.offer("data-" + i);
        }

        final ArrayList<String> strings = new ArrayList<>();
        data.drainTo(strings,3);
        System.out.println(strings);
    }
}
