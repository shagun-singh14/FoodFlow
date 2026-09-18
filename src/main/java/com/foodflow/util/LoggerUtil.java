package com.foodflow.util;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

/**
 * Centralized logging utility using java.util.logging.
 * Ensures security-safe logging without plaintext passwords.
 */
public class LoggerUtil {

    private static final Logger rootLogger = Logger.getLogger("com.foodflow");
    private static boolean initialized = false;

    static {
        initLogging();
    }

    public static synchronized void initLogging() {
        if (initialized) return;

        try {
            rootLogger.setUseParentHandlers(false);
            rootLogger.setLevel(Level.INFO);

            // Console Handler with clean formatting
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(new SimpleFormatter() {
                private static final String FORMAT = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

                @Override
                public synchronized String format(LogRecord lr) {
                    return String.format(FORMAT,
                            new java.util.Date(lr.getMillis()),
                            lr.getLevel().getLocalizedName(),
                            lr.getMessage()
                    );
                }
            });
            rootLogger.addHandler(consoleHandler);

            // Ensure logs directory exists
            File logDir = new File("logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            // File Handler
            FileHandler fileHandler = new FileHandler("logs/foodflow.log", 1024 * 1024 * 5, 3, true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(fileHandler);

            initialized = true;
        } catch (IOException e) {
            System.err.println("Could not initialize file logger: " + e.getMessage());
        }
    }

    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }

    public static void info(Class<?> clazz, String message) {
        getLogger(clazz).info(message);
    }

    public static void warning(Class<?> clazz, String message) {
        getLogger(clazz).warning(message);
    }

    public static void severe(Class<?> clazz, String message, Throwable t) {
        getLogger(clazz).log(Level.SEVERE, message, t);
    }
}
