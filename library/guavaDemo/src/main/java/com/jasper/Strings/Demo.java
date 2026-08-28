package com.jasper.Strings;


import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("ConstantConditions")
@Slf4j
public class Demo {
    public static void main(String[] args) {
        String  a = " ";
//        不包含空字符串
        final boolean nullOrEmpty = Strings.isNullOrEmpty(a);
        log.info("nullOrEmpty: {}",nullOrEmpty);
    }
}
