package org.example.clean4u._core.utils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter BIRTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static String timestampFormat(Timestamp time) {
        if (time == null) {
            return null;
        }
        return time.toLocalDateTime().format(FORMATTER);
    }

    public static String birthFormat(LocalDate time) {
        if (time == null) {
            return null;
        }
        return time.format(BIRTH_FORMATTER);
    }

    public static String timeFormat(LocalTime time) {
        if (time == null) {
            return null;
        }
        return time.format(TIME_FORMATTER);
    }
}
