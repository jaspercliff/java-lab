package jasper.basic.dataType.bigdecimal;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author jasper
 * @since 2026-07-18 16:14:44 <br>
 */
@Slf4j
public class BigDecimalDemo {
    public static void main(String[] args) {
//        优先使用字符串构造 BigDecimal，避免先经过 double 的精度损失
        // BigDecimal s = new BigDecimal(1.0); // 直接传double 会将double原来的误差带进来
        BigDecimal a = new BigDecimal("0.1");
        BigDecimal b = new BigDecimal("0.10");
        // BigDecimal c = BigDecimal.valueOf(0.10);
//        数值（unscaled value 与 scale 共同决定）：表示实际数值大小。
//        scale（小数位数尺度）：决定小数点的位置及表示形式
        log.info("{}", a.equals(b)); //false  比较数值和 scale 是否一致
        log.info("{}",a.compareTo(b)); //0
        BigDecimal divide = a.divide(b, RoundingMode.HALF_UP);
        log.info("{}", divide);
    }
}
