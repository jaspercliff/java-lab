package com.jasper.lang;

import java.util.Calendar;
import java.util.Date;

public class DateUtilsOld {

    /**
     * 获取指定日期所在月份的第一天（Date 类型）。
     *
     * @param date 指定的日期
     * @return 指定日期所在月份的第一天
     */
    public static Date firstDayOfMonth(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.set(instance.get(Calendar.YEAR), instance.get(Calendar.MONTH), 1);
        return instance.getTime();
    }

    /**
     * 获取指定日期所在月份的最后一天（Date 类型）。
     *
     * @param date 指定的日期
     * @return 指定日期所在月份的最后一天
     */
    public static Date LastDayOfMonth(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.set(instance.get(Calendar.YEAR), instance.get(Calendar.MONTH), 1);
        instance.add(Calendar.MONTH, 1); // 下个月第一天
        instance.add(Calendar.DAY_OF_MONTH, -1);
        return instance.getTime();
    }

    /**
     * 获取指定日期是所在月份的第几天（Date 类型）。
     *
     * @param date 指定的日期
     * @return 指定日期是所在月份的第几天
     */
    public static int getDayOfMonth(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return instance.get(Calendar.DAY_OF_MONTH);
    }
}
