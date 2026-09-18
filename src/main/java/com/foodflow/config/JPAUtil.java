package com.foodflow.config;

import com.foodflow.util.LoggerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton utility managing the JPA EntityManagerFactory.
 * Demonstrates:
 * - Singleton pattern
 * - Jakarta Persistence (JPA 3.1) Lifecycle & EntityManager management
 * - Dynamic persistence properties injection
 */
public class JPAUtil {

    private static volatile EntityManagerFactory emf;

    private JPAUtil() {
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            synchronized (JPAUtil.class) {
                if (emf == null) {
                    try {
                        Map<String, Object> configOverrides = new HashMap<>();

                        if (DatabaseManager.getInstance().isFallbackMode()) {
                            // Configure Hibernate for H2 In-Memory
                            configOverrides.put("jakarta.persistence.jdbc.driver", AppConfig.getFallbackDriver());
                            configOverrides.put("jakarta.persistence.jdbc.url", AppConfig.getFallbackUrl());
                            configOverrides.put("jakarta.persistence.jdbc.user", AppConfig.getFallbackUsername());
                            configOverrides.put("jakarta.persistence.jdbc.password", AppConfig.getFallbackPassword());
                            configOverrides.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
                            configOverrides.put("hibernate.hbm2ddl.auto", "update");
                        } else {
                            configOverrides.put("jakarta.persistence.jdbc.driver", AppConfig.getDbDriver());
                            configOverrides.put("jakarta.persistence.jdbc.url", AppConfig.getDbUrl());
                            configOverrides.put("jakarta.persistence.jdbc.user", AppConfig.getDbUsername());
                            configOverrides.put("jakarta.persistence.jdbc.password", AppConfig.getDbPassword());
                        }

                        emf = Persistence.createEntityManagerFactory("foodflow-pu", configOverrides);
                        LoggerUtil.info(JPAUtil.class, "EntityManagerFactory created successfully.");
                    } catch (Exception e) {
                        LoggerUtil.severe(JPAUtil.class, "Failed to initialize JPA EntityManagerFactory", e);
                        throw new RuntimeException("JPA Initialization Error", e);
                    }
                }
            }
        }
        return emf;
    }

    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            LoggerUtil.info(JPAUtil.class, "EntityManagerFactory closed.");
        }
    }
}
