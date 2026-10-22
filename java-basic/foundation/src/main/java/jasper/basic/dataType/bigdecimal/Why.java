package jasper.basic.dataType.bigdecimal;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class Why {
    public static void main(String[] args) {
        // 因为 double 使用二进制浮点数表示数值，而某些十进制小数无法用有限位二进制精确表
        log.info("{}",0.1+0.2); // 0.30000000000000004
        BigDecimal a = new BigDecimal("0.1");
        BigDecimal b = new BigDecimal("0.2");
        log.info("{}",a.add(b));
    }
}
