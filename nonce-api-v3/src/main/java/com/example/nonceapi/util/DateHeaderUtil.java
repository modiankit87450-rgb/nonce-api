package com.example.nonceapi.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateHeaderUtil {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    /**
     * Validate date within ±1 minute
     */
    public boolean isDateValid(String dateHeader) {
        try {
            LocalDateTime requestTime = LocalDateTime.parse(dateHeader, FORMATTER);
            LocalDateTime currentTime = LocalDateTime.now();

            long diffSeconds = Math.abs(
                    java.time.Duration.between(requestTime, currentTime).getSeconds()
            );

            return diffSeconds <= 60;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generate current date string
     */
    public String generateCurrentDate() {
        return LocalDateTime.now().format(FORMATTER);
    }
}