package com.foodflow.report;

import com.foodflow.model.interfaces.Exportable;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Abstract base class for all generated academic & billing reports in FoodFlow.
 * Demonstrates:
 * - Abstract class in OOP design hierarchy
 * - Implementation of Exportable interface
 * - Template method pattern for report generation
 */
public abstract class Report implements Exportable, Serializable {

    private static final long serialVersionUID = 1L;

    private final String reportTitle;
    private final LocalDateTime generatedAt;
    private final String generatedBy;

    public Report(String reportTitle, String generatedBy) {
        this.reportTitle = reportTitle;
        this.generatedBy = generatedBy != null ? generatedBy : "System";
        this.generatedAt = LocalDateTime.now();
    }

    /**
     * Abstract method generating the formatted body content of the report.
     * Demonstrates Polymorphism across subclass report types.
     */
    public abstract String generateBody();

    /**
     * Abstract method returning high-level summary metrics.
     */
    public abstract String getMetricsSummary();

    @Override
    public String toFormattedText() {
        StringBuilder sb = new StringBuilder();
        sb.append("======================================================================\n");
        sb.append(" ").append(reportTitle.toUpperCase()).append("\n");
        sb.append(" FoodFlow - College Mess Management System\n");
        sb.append(" Generated: ").append(generatedAt).append(" | By: ").append(generatedBy).append("\n");
        sb.append("======================================================================\n\n");
        sb.append(generateBody()).append("\n");
        sb.append("----------------------------------------------------------------------\n");
        sb.append(" SUMMARY: ").append(getMetricsSummary()).append("\n");
        sb.append("======================================================================\n");
        return sb.toString();
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }
}
