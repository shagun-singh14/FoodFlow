package com.foodflow.model.enums;

/**
 * Enumeration representing categories of mess grievances.
 */
public enum ComplaintCategory {
    FOOD_QUALITY("Food Quality"),
    HYGIENE("Hygiene & Cleanliness"),
    MENU("Menu Variety / Special Requests"),
    TIMING("Counter & Service Timings"),
    STAFF("Staff Behavior / Service"),
    OTHER("Other Issues");

    private final String label;

    ComplaintCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ComplaintCategory fromString(String catStr) {
        if (catStr == null) return OTHER;
        try {
            return ComplaintCategory.valueOf(catStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}
