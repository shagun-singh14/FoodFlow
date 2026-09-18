package com.foodflow;

/**
 * Standard JavaFX Application Launcher.
 * This class does NOT extend javafx.application.Application directly,
 * bypassing the Java 11+ launcher check when running from a standard classpath.
 */
public class Main {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
