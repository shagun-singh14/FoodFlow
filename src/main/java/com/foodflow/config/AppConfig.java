package com.foodflow.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Application configuration loader reading from application.properties.
 */
public class AppConfig {

    private static final Properties props = new Properties();

    static {
        try (InputStream is = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load application.properties, using fallback defaults: " + e.getMessage());
        }
    }

    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        String val = props.getProperty(key);
        if (val == null) return defaultValue;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static double getDouble(String key, double defaultValue) {
        String val = props.getProperty(key);
        if (val == null) return defaultValue;
        try {
            return Double.parseDouble(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String val = props.getProperty(key);
        if (val == null) return defaultValue;
        return Boolean.parseBoolean(val.trim());
    }

    public static String getDbDriver() {
        return get("db.driver", "com.mysql.cj.jdbc.Driver");
    }

    public static String getDbUrl() {
        return get("db.url", "jdbc:mysql://localhost:3306/foodflow_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
    }

    public static String getDbUsername() {
        return get("db.username", "root");
    }

    public static String getDbPassword() {
        return get("db.password", "root");
    }

    public static String getFallbackDriver() {
        return get("db.fallback.driver", "org.h2.Driver");
    }

    public static String getFallbackUrl() {
        return get("db.fallback.url", "jdbc:h2:mem:foodflow_db;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_LOWER=TRUE");
    }

    public static String getFallbackUsername() {
        return get("db.fallback.username", "sa");
    }

    public static String getFallbackPassword() {
        return get("db.fallback.password", "");
    }

    public static boolean isFallbackEnabled() {
        return getBoolean("db.fallback.enabled", true);
    }

    public static double getMealRate(String mealType) {
        return getDouble("meal.rate." + mealType.toLowerCase(), 30.0);
    }

    public static String getReportsExportDir() {
        return get("reports.export.dir", "reports/");
    }

    public static String getSecuritySalt() {
        return get("security.salt", "foodflow_salt_2026");
    }
}
