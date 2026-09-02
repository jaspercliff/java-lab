package com;

import com.jasper.lang.StrUtil;
import org.junit.jupiter.api.Test;

public class StrUtilsTest {

    @Test
    public void test(){
        String str = " 123456 ";
        System.out.println("str = " + str);
        String s = StrUtil.stringTrim(str);
        System.out.println("s = " + s);
        String s2 = " ";
        System.out.println("isEmpty = " + StrUtil.isEmpty(s2));

        String s3 = "jasper";
        String s4 = "jasper";
//        System.out.println("isEquals = " + StrUtil.isEquals(s3, s4));
    }
}
