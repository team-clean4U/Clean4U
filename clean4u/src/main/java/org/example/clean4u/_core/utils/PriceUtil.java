package org.example.clean4u._core.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class PriceUtil {
    public static String format (int price) {
        NumberFormat krw = NumberFormat.getIntegerInstance(Locale.KOREA);
        return krw.format(price);
    }
}
