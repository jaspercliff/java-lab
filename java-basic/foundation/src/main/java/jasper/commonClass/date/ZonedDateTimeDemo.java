package jasper.commonClass.date;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
public class ZonedDateTimeDemo {
    public static void main(String[] args) {
        //同一时刻在不同时区显示的时间
        ZonedDateTime now = ZonedDateTime.now();

        log.info("now={}", now);
        ZoneId zone = now.getZone();
        log.info("zone={}", zone);

        ZonedDateTime losAngeles =
                ZonedDateTime.now(ZoneId.of("America/Los_Angeles"));
        log.info("losAngeles={}", losAngeles);

        LocalDateTime now1 = LocalDateTime.now();
        log.info("now1={}", now1);
        ZonedDateTime zonedDateTime = now1.atZone(ZoneId.of("America/Los_Angeles"));
        log.info("zonedDateTime={}", zonedDateTime);
    }
}
