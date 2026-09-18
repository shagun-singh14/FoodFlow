package com.foodflow.model.enums;

/**
 * Enumeration representing the four standard college mess meal sessions.
 */
public enum MealType {
    BREAKFAST("Breakfast", 35.0, "07:30 - 09:30"),
    LUNCH("Lunch", 50.0, "12:00 - 14:00"),
    SNACKS("Snacks", 25.0, "16:30 - 18:00"),
    DINNER("Dinner", 45.0, "19:30 - 21:30");

    private final String label;
    private final double standardRate;
    private final String timing;

    MealType(String label, double standardRate, String timing) {
        this.label = label;
        this.standardRate = standardRate;
        this.timing = timing;
    }

    public String getLabel() {
        return label;
    }

    public double getStandardRate() {
        return standardRate;
    }

    public String getTiming() {
        return timing;
    }

    public static MealType fromString(String typeStr) {
        if (typeStr == null) return BREAKFAST;
        try {
            return MealType.valueOf(typeStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return BREAKFAST;
        }
    }
}
