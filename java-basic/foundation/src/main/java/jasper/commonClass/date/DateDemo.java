package jasper.commonClass.date;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateDemo {
    public static void main(String[] args) {
        LocalDate localDate = LocalDate.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        final String format = formatter.format(localDate);
        System.out.println(format);
    }
}
