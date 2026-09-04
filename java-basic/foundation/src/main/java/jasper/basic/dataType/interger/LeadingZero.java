package jasper.basic.dataType.interger;

/**
 * @author jasper
 * @since 2026-05-10 20:17:27
 */
public class LeadingZero {
    public static void main(String[] args) {
        // int 32 从左边最高位开始，到第一个1前面有多少个0
        // 00000000 00000000 00000000 00000001
        int numberOfLeadingZeros = Integer.numberOfLeadingZeros(1);
        System.out.println("numberOfLeadingZeros = " + numberOfLeadingZeros);

        int numberOfLeadingZeros2 = Integer.numberOfLeadingZeros(16);
        System.out.println("numberOfLeadingZeros2 = " + numberOfLeadingZeros2);
        System.out.println(Integer.toBinaryString(numberOfLeadingZeros2));
    }
}
