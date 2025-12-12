package org.example.clean4u.utils;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static String timestampFormat(Timestamp time) {
        if (time == null) {
            return null;
        }
        return time.toLocalDateTime().format(FORMATTER);
    }
}
