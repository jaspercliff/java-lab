package jasper.commonClass.date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarDemo {
    public static void main(String[] args) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY)-1);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        final String format = sdf.format(calendar.getTime());

        final String queryDate = format.substring(0, 8);
        String startTime = format.substring(8,10)+"0000";
        String endTime = format.substring(8,10)+"5959";
    }
}
