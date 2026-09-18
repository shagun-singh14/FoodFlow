package com.foodflow.model.enums;

/**
 * Enumeration representing the lifecycle states of a student mess complaint.
 */
public enum ComplaintStatus {
    OPEN("Open", "badge-warning"),
    IN_PROGRESS("In Progress", "badge-info"),
    RESOLVED("Resolved", "badge-success"),
    REJECTED("Rejected", "badge-danger");

    private final String display;
    private final String cssClass;

    ComplaintStatus(String display, String cssClass) {
        this.display = display;
        this.cssClass = cssClass;
    }

    public String getDisplay() {
        return display;
    }

    public String getCssClass() {
        return cssClass;
    }

    public static ComplaintStatus fromString(String statusStr) {
        if (statusStr == null) return OPEN;
        try {
            return ComplaintStatus.valueOf(statusStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return OPEN;
        }
    }
}
