package com.example.webapp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerUtils {
    private static final Logger logger = LoggerFactory.getLogger(TimerUtils.class);
    private final long startTime;

    private TimerUtils() {
        long now;
        try {
            now = System.nanoTime();
        } catch (Exception e) {
            logger.error("Error starting TimerUtils: {}", e.getMessage());
            now = 0;
        }
        this.startTime = now;
    }

    public static TimerUtils start() {
        try {
            return new TimerUtils();
        } catch (Exception e) {
            logger.error("Exception in TimerUtils.start(): {}", e.getMessage());
            return new TimerUtils() {
                @Override
                public long elapsedMillis() {
                    return 0L;
                }
            };
        }
    }

    public long elapsedMillis() {
        try {
            long endTime = System.nanoTime();
            return (endTime - this.startTime) / 1_000_000;
        } catch (Exception e) {
            logger.error("Error calculating elapsed time: {}", e.getMessage());
            return 0L;
        }
    }
}
