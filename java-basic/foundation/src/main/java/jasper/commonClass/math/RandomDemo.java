package jasper.commonClass.math;

public class RandomDemo {
    public static void main(String[] args) {
        double random = Math.random(); // [0.0 - 1.0)
        System.out.println((int) (random * 100)); // 0 -99
    }
}
