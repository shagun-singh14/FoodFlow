package com.foodflow.model.interfaces;

/**
 * Interface contract for system entities that can be managed, searched, and updated in administrative workflows.
 */
public interface Manageable {
    Long getId();
    String getDisplayName();
    String getDetailsSummary();
}
