package org.example.clean4u._core.utils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter BIRTHFORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
        return time.format(BIRTHFORMATTER);
    }
}
