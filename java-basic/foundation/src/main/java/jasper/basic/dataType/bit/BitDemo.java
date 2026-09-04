package jasper.basic.dataType.bit;

/**
 * @author jasper
 * @since 2026-05-15 16:20:59
 */
public class BitDemo {
    public static void main(String[] args) {
        int n = 8; // 1000
        int a = n >>> 3; // ==n/8
        System.out.println("a = " + a); // 1
        int b = n << 1;
        System.out.println("b = " + b); // n * 2  1000  -> 1 0000
        int c = 1 << 15;
        System.out.println("c = " + c);
        System.out.println(Integer.toBinaryString(c));
    }
}
