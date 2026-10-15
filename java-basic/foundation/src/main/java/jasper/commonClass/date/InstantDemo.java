package jasper.commonClass.date;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

/**
 * 表示时间线上的一个确定时刻，通常可以理解为 UTC 时间轴上的一个时间点
 * LocalDateTime：现在是几点，但不知道在哪个时区。
 * ZonedDateTime：现在是几点，并且知道在哪个时区。
 * Instant：确定某件事情究竟发生在什么时候
 */
@Slf4j
public class InstantDemo {
    public static void main(String[] args) {
        Instant now = Instant.now();
        log.info("now={}", now); // Z 表示 utc(全球统一使用的一套时间基准)

//        Unix 时间起点：1970 年 1 月 1 日 00:00:00 UTC。
//        Unix 时间戳：从这个起点计算出的秒数或毫秒数
        long seconds = now.getEpochSecond(); //表示从 Unix 时间起点经过了多少整秒。
        long millis = now.toEpochMilli(); //表示从 Unix 时间起点经过了多少毫秒
        log.info("seconds={}", seconds);
        log.info("millis={}", millis);
    }
}
