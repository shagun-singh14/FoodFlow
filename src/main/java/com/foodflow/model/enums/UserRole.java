package com.foodflow.model.enums;

/**
 * Enumeration representing user security and operational roles in FoodFlow.
 */
public enum UserRole {
    STUDENT("Student"),
    ADMIN("Mess Admin");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UserRole fromString(String roleStr) {
        if (roleStr == null) return STUDENT;
        try {
            return UserRole.valueOf(roleStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return STUDENT;
        }
    }
}
