package com;

import com.jasper.lang.DateUtil;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class DateUtilTest {

    @Test
    public void testExceed(){
        final LocalDate localDate = LocalDate.now().plusDays(29);
        final boolean isExceed = DateUtil.isExceedLimit(localDate, 30, ChronoUnit.DAYS);
        // System.out.println(STR."Is exceed limit: \{isExceed}");
        final LocalDate localDate1 = LocalDate.now().plusDays(30);
        final boolean exceedLimit = DateUtil.isExceedLimit(localDate1, 30, ChronoUnit.DAYS);
        // System.out.println(STR."exceedLimit = \{exceedLimit}");
    }

    @Test
    void getDiffDays_handlesNullDates() {
        final LocalDate start = LocalDate.now();
        final LocalDate  end= LocalDate.now().plusDays(1);
        final BigDecimal diffDays = DateUtil.getDiffDays(start, end);
        // System.out.println(STR."diffDays = \{diffDays}");
        final LocalDate start1 = LocalDate.now();
        final LocalDate  end1= LocalDate.now().minusDays(1);
        final BigDecimal diffDays1 = DateUtil.getDiffDays(start1, end1);
        // System.out.println(STR."diffDays1 = \{diffDays1}");
        assertThrows(NullPointerException.class, () -> DateUtil.getDiffDays(LocalDate.now(), null));
    }

}