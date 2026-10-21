package jasper.commonClass.math;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jasper
 * @since 2026-04-23 14:59:46
 */
@Slf4j
public class Operation {
    public static void main(String[] args) {
        // 向上取整
        log.info("ceil = {}", Math.ceil(2.3));
        log.info("ceil2 = {}", Math.ceil(133.3));

//        如果输入值 小于 min，它就返回 min。
//        如果输入值 大于 max，它就返回 max。
//        如果输入值 在两者之间，它就返回原值
        int clamp = Math.clamp(80, 0, 100);
        log.info("clamp = {}", clamp);
        int clamp1 = Math.clamp(110, 0, 100);
        log.info("clamp1 = {}", clamp1);
        int clamp2 = Math.clamp(-10, 0, 100);
        log.info("clamp2 = {}", clamp2);
    }
}
