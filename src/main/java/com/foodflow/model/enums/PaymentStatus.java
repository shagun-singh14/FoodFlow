package com.foodflow.model.enums;

/**
 * Enumeration representing monthly mess billing payment states.
 */
public enum PaymentStatus {
    PENDING("Pending", "badge-warning"),
    PAID("Paid", "badge-success"),
    OVERDUE("Overdue", "badge-danger"),
    WAIVED("Waived", "badge-info");

    private final String label;
    private final String badgeClass;

    PaymentStatus(String label, String badgeClass) {
        this.label = label;
        this.badgeClass = badgeClass;
    }

    public String getLabel() {
        return label;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public static PaymentStatus fromString(String statusStr) {
        if (statusStr == null) return PENDING;
        try {
            return PaymentStatus.valueOf(statusStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }
}
