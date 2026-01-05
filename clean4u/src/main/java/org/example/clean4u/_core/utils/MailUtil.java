package org.example.clean4u._core.utils;

import java.util.Random;

public class MailUtil {
    public static String generateRandomCode() {
        Random random = new Random();
        int code = 100_000 + random.nextInt(900_000);

        return String.valueOf(code);
    }
}
