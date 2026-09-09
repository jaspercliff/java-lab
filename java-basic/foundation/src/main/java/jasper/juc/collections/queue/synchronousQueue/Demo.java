package jasper.juc.collections.queue.synchronousQueue;

import java.util.concurrent.SynchronousQueue;

/**
 * 不想让任务堆积在队列里，我希望生产者和消费者直接交接
 * SynchronousQueue 是 Java 并发包中的一个阻塞队列，容量为 0，不存储元素。它的核心机制是生产者和消费者之间的直接交接。
 * 生产者调用 put() 时，如果没有消费者接收，就会阻塞；消费者调用 take() 时，如果没有生产者提供元素，也会阻塞
 */
public class Demo {

    public static void main(String[] args) throws InterruptedException {
        SynchronousQueue<String> queue = new SynchronousQueue<>();

        Thread producer = new Thread(() -> {
            try {
                System.out.println("生产者：准备放入订单A");

                queue.put("订单A");

                System.out.println("生产者：订单A已经被接收");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                Thread.sleep(2000);

                System.out.println("消费者：准备接收订单");
//                如果消费者还没有执行 take()，生产者的 put() 就会阻塞
                String order = queue.take();

                System.out.println("消费者：收到 " + order);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
    }
}