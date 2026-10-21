package jasper.commonClass.math;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class RandomDemo {
    public static void main(String[] args) {
        double random = Math.random(); // [0.0 - 1.0)
        log.info("random:{}", (int) (random * 100)); // 0 -99
        Random random1 = new Random();
        int i = random1.nextInt(100);// 0 - 99
        log.info("random:{}", i);
    }
}
