package jasper.juc.tools.countdownlatch;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.CountDownLatch;

@Data
@AllArgsConstructor
public class Worker implements Runnable {
    private int id;
    private CountDownLatch latch;

    @Override
    public void run() {
        try {
            Thread.sleep((long) (Math.random() * 1000));
            System.out.println("thread: " + id + " is done");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            latch.countDown();
        }
    }
}
