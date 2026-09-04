package jasper.basic.control;

/**
 * @author jasper
 * @since 2026-06-23 10:58:29 <br>
 */
public class OddEvenJudge {
    public static void main(String[] args) {
        int a = 5, b = 6;
        System.out.println((a & 1) == 1); // odd
        System.out.println((b & 1) == 1); // even
    }
}
