package com;

import com.jasper.api.IEnum;
import com.jasper.constants.ThreadType;
import org.junit.jupiter.api.Test;

public class IEnumTest {

    @Test
    public void testParse() {
        ThreadType virtual = IEnum.parseOf(ThreadType.class, "virtual");
        System.out.println(virtual.getDesc());

        ThreadType platform = IEnum.parseOf(ThreadType.class, "PlatForm  ");
        System.out.println(platform.getDesc());

        ThreadType platform1 = IEnum.parseOf(ThreadType.class, "PlatForm  1");
        System.out.println(platform1.getDesc());
    }
}
