package com.example.nonceapi.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DateHeaderUtil {

    private static final Logger log = LoggerFactory.getLogger(DateHeaderUtil.class);

    // Allowed drift = ±30 seconds in milliseconds
    private static final long ALLOWED_DRIFT_MS = 30_000L;

    /**
     * Current time ko hex format mein encode karo
     * Example output: "0000019d47ffe6f2"
     */
    public String generateHexDate() {
        long nowMs = System.currentTimeMillis();
        return String.format("%016x", nowMs);
    }

    /**
     * Hex Date header ko validate karo
     * - Decode hex -> milliseconds
     * - Server time se compare karo
     * - Agar ±30 seconds ke bahar hai -> false return karo
     *
     * @param hexDate - header value like "0000019d47ffe6f2"
     * @return true if valid, false if tampered or too old/future
     */
    public boolean isDateValid(String hexDate) {
        if (hexDate == null || hexDate.isEmpty()) {
            log.warn("Date header missing");
            return false;
        }

        try {
            long requestTimeMs = Long.parseUnsignedLong(hexDate, 16);
            long serverTimeMs  = System.currentTimeMillis();
            long diffMs        = Math.abs(serverTimeMs - requestTimeMs);

            log.debug("Date validation | requestTime={} ms, serverTime={} ms, diff={} ms",
                    requestTimeMs, serverTimeMs, diffMs);

            if (diffMs > ALLOWED_DRIFT_MS) {
                log.warn("Date header out of range! diff={}ms (allowed ±30000ms)", diffMs);
                return false;
            }

            return true;

        } catch (NumberFormatException e) {
            log.error("Invalid hex Date header: {}", hexDate);
            return false;
        }
    }
}
