package jasper.lang;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * 日期工具类，提供常用的日期操作方法。
 *
 * @version 1.0
 * @author jasper
 * @date 2024-11-05
 */
public class DateUtil {
    private static final String SIMPLE_PATTERN = "yyyyMMdd";
    private static final String DEFAULT_PATTERN = SIMPLE_PATTERN;

    /**
     * 获取指定日期所在月份的第一天。
     *
     * @param date 指定的日期
     * @return 指定日期所在月份的第一天
     */
    public static LocalDate firstDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取指定日期所在月份的最后一天。
     *
     * @param date 指定的日期
     * @return 指定日期所在月份的最后一天
     */
    public static LocalDate LastDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取指定日期是所在月份的第几天。
     *
     * @param date 指定的日期
     * @return 指定日期是所在月份的第几天
     */
    public static int getDayOfMonth(LocalDate date) {
        return date.getDayOfMonth();
    }

    /**
     * 判断指定日期是否超过限制。
     *
     * @param date 指定日期
     * @param limit 超过限制的天数
     * @param unit 时间单位
     * @return 如果指定日期超过限制返回 true，否则返回 false
     *     <p>示例： 假设当前日期是 2024-12-31，limit=30，unit=DAYS： - 2025-01-30（now + 30） → 返回 false（未超过） -
     *     2025-01-31（now + 30 天后的一天） → 返回 true（超过） 不包括=加30天的
     */
    public static boolean isExceedLimit(LocalDate date, int limit, ChronoUnit unit) {
        if (date == null) {
            return false;
        }
        final LocalDate now = LocalDate.now();
        final LocalDate limitTime = now.plus(limit, unit);
        return !date.isBefore(limitTime);
    }

    /**
     * 计算两个日期之间的天数差。
     *
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @return 两个日期之间的天数差，保留两位小数
     */
    public static BigDecimal getDiffDays(LocalDate date1, LocalDate date2) {
        final long between = ChronoUnit.DAYS.between(date1, date2);
        return new BigDecimal(between).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 将字符串格式的日期转换为 LocalDate。
     *
     * @param dateStr 日期字符串，格式为 yyyyMMdd
     * @return 转换后的 LocalDate 对象，如果解析失败则返回 null
     */
    public static LocalDate getLocalDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate = null;

        try {
            localDate = LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            // 处理解析异常，例如记录日志或抛出自定义异常
        }

        return localDate;
    }

    public static boolean isWithinOneYear(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            return false;
        }
        LocalDate oneYearLater = startDate.plusYears(1);
        return !endDate.isAfter(oneYearLater); // 结束日期不能晚于开始日期加一年
    }
}

