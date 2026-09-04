package jasper.basic.dataType.bigdecimal;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author jasper
 * @since 2026-07-18 16:14:44 <br>
 */
public class BigDecimalDemo {
    public static void main(String[] args) {
        // BigDecimal s = new BigDecimal(1.0); // 直接传double 会将double原来的误差带进来
        BigDecimal a = new BigDecimal("0.1");
        BigDecimal b = new BigDecimal("0.10");
        // BigDecimal c = BigDecimal.valueOf(0.10);
        System.out.println(a.equals(b));
        System.out.println(a.compareTo(b) == 0);
        BigDecimal divide = a.divide(b, RoundingMode.HALF_UP);
        System.out.println("divide = " + divide);
    }
}
