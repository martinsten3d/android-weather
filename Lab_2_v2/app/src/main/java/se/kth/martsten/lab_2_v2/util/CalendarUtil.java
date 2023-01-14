package se.kth.martsten.lab_2_v2.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

/**
 * Util class for Calendar objects.
 */
public class CalendarUtil {

    /**
     * Convert a dateTime string to a java Calendar.
     * @param dateTimeString the string to be converted.
     * @return a new Calendar object set to the time and date passed by the string.
     */
    public static Calendar convertToCalendar(String dateTimeString) {
        Calendar cal = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            cal.setTime(Objects.requireNonNull(sdf.parse(dateTimeString)));
            return cal;
        }
        catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return cal;
    }
}
