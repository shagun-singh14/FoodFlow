package com.foodflow.config;

import com.foodflow.exception.DatabaseException;
import com.foodflow.util.LoggerUtil;
import com.foodflow.util.PasswordUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.stream.Collectors;

/**
 * Singleton Database Connection Manager implementing direct JDBC persistence.
 * Demonstrates:
 * - Singleton Design Pattern (Double-checked locking, thread-safe)
 * - JDBC API (Connection, Statement, PreparedStatement, ResultSet, SQLException)
 * - Zero-Config Auto Failover to in-memory H2 if MySQL is unavailable
 */
public class DatabaseManager {

    private static volatile DatabaseManager instance;

    private boolean isFallbackMode = false;
    private boolean initialized = false;

    private DatabaseManager() {
        initDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    private Connection keepAliveConnection;

    private synchronized void initDatabase() {
        if (initialized) return;

        // 1. Try MySQL First
        try {
            Class.forName(AppConfig.getDbDriver());
            try (Connection conn = DriverManager.getConnection(
                    AppConfig.getDbUrl(), AppConfig.getDbUsername(), AppConfig.getDbPassword())) {
                LoggerUtil.info(DatabaseManager.class, "Successfully connected to MySQL database: " + AppConfig.getDbUrl());
                isFallbackMode = false;
                initialized = true;
                return;
            }
        } catch (Exception mysqlEx) {
            LoggerUtil.warning(DatabaseManager.class,
                    "MySQL not reachable (" + mysqlEx.getMessage() + "). Initiating Zero-Config Embedded Database fallback.");
        }

        // 2. Fallback to H2 in-memory DB if MySQL not available
        if (AppConfig.isFallbackEnabled()) {
            try {
                Class.forName(AppConfig.getFallbackDriver());
                keepAliveConnection = DriverManager.getConnection(
                        AppConfig.getFallbackUrl(), AppConfig.getFallbackUsername(), AppConfig.getFallbackPassword());
                isFallbackMode = true;
                LoggerUtil.info(DatabaseManager.class, "Connected to Embedded In-Memory Database (" + AppConfig.getFallbackUrl() + ").");
                executeSchemaInit(keepAliveConnection);
                initialized = true;
                return;
            } catch (Exception h2Ex) {
                LoggerUtil.severe(DatabaseManager.class, "Failed to initialize fallback database", h2Ex);
            }
        }

        initialized = true;
    }

    /**
     * Executes the SQL schema & seed data script on the connected database.
     */
    private void executeSchemaInit(Connection conn) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("foodflow_database.sql");
            if (is == null) {
                java.io.File sqlFile = new java.io.File("sql/foodflow_database.sql");
                if (sqlFile.exists()) {
                    is = new java.io.FileInputStream(sqlFile);
                }
            }

            if (is != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    StringBuilder fullSql = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String trimmed = line.trim();
                        if (trimmed.startsWith("--") || trimmed.startsWith("/*") || trimmed.startsWith("*")) {
                            continue;
                        }
                        fullSql.append(line).append("\n");
                    }

                    String defaultHash = PasswordUtil.hashPassword("Password@123", "foodflow_salt_2026");

                    String[] statements = fullSql.toString().split(";");
                    try (Statement stmt = conn.createStatement()) {
                        for (String sql : statements) {
                            String trimmed = sql.trim();
                            if (trimmed.isEmpty()) continue;

                            String upper = trimmed.toUpperCase();
                            if (upper.startsWith("USE ") || upper.startsWith("CREATE DATABASE ") || upper.startsWith("DROP DATABASE ")) {
                                continue;
                            }

                            // Sanitize MySQL-specific constructs for H2
                            if (isFallbackMode) {
                                trimmed = trimmed.replaceAll("(?i)ENGINE\\s*=\\s*InnoDB", "");
                                trimmed = trimmed.replaceAll("(?i),?\\s*INDEX\\s+[a-zA-Z0-9_]+\\s*\\([^)]+\\)", "");
                            }

                            // Ensure consistent password hash for seed data
                            if (trimmed.contains("INSERT INTO users")) {
                                trimmed = trimmed.replaceAll("(?i)'[0-9a-f]{64}'", "'" + defaultHash + "'");
                            }

                            try {
                                stmt.execute(trimmed);
                            } catch (SQLException e) {
                                LoggerUtil.warning(DatabaseManager.class, "Seed execution note on: [" +
                                        trimmed.substring(0, Math.min(trimmed.length(), 60)) + "...] -> " + e.getMessage());
                            }
                        }
                    }
                }
                LoggerUtil.info(DatabaseManager.class, "Embedded database schema & seed data initialized successfully.");
            }
        } catch (Exception e) {
            LoggerUtil.severe(DatabaseManager.class, "Error executing schema initialization", e);
        }
    }

    /**
     * Obtains a fresh JDBC connection.
     */
    public Connection getConnection() throws SQLException {
        if (isFallbackMode) {
            return DriverManager.getConnection(
                    AppConfig.getFallbackUrl(), AppConfig.getFallbackUsername(), AppConfig.getFallbackPassword());
        } else {
            return DriverManager.getConnection(
                    AppConfig.getDbUrl(), AppConfig.getDbUsername(), AppConfig.getDbPassword());
        }
    }

    public boolean isFallbackMode() {
        return isFallbackMode;
    }

    /**
     * Utility method to safely close JDBC resources.
     */
    public static void close(AutoCloseable... resources) {
        for (AutoCloseable res : resources) {
            if (res != null) {
                try {
                    res.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
