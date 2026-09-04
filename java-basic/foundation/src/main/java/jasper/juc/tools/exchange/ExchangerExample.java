package jasper.juc.tools.exchange;

import java.util.concurrent.Exchanger;

public class ExchangerExample {
    public static void main(String[] args) {
        // 俩个线程在一个同步点进行交换数据，是整个对象交换 blockingqueue一个一个  只能俩个线程
        final Exchanger<String> exchanger = new Exchanger<>();

        Thread producer =
                new Thread(
                        () -> {
                            try {
                                String generatedData = "Data from Producer";
                                System.out.println("Producer before exchange: " + generatedData);
                                // 交换数据
                                generatedData = exchanger.exchange(generatedData);
                                System.out.println("Producer after exchange: " + generatedData);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        });

        Thread consumer =
                new Thread(
                        () -> {
                            try {
                                String receivedData = "Data from Consumer";
                                System.out.println("Consumer before exchange: " + receivedData);
                                // 交换数据
                                receivedData = exchanger.exchange(receivedData);
                                System.out.println("Consumer after exchange: " + receivedData);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        });

        producer.start();
        consumer.start();
    }
}
