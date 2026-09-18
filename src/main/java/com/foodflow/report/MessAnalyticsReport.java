package com.foodflow.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Concrete report summarizing overall mess operations, ratings, complaints, and finances.
 * Demonstrates:
 * - Inheritance (extends Report)
 * - Method Overriding & Polymorphism
 */
public class MessAnalyticsReport extends Report {

    private final LocalDate periodDate;
    private final int totalEnrolledStudents;
    private final int todayMealsServed;
    private final double averageFoodRating;
    private final int openComplaintsCount;
    private final BigDecimal totalMonthlyRevenue;
    private final Map<String, Integer> mealTypeBreakdown;

    public MessAnalyticsReport(LocalDate periodDate, int totalEnrolledStudents, int todayMealsServed,
                               double averageFoodRating, int openComplaintsCount, BigDecimal totalMonthlyRevenue,
                               Map<String, Integer> mealTypeBreakdown, String generatedBy) {
        super("Mess Operational & Analytics Report - " + periodDate, generatedBy);
        this.periodDate = periodDate;
        this.totalEnrolledStudents = totalEnrolledStudents;
        this.todayMealsServed = todayMealsServed;
        this.averageFoodRating = averageFoodRating;
        this.openComplaintsCount = openComplaintsCount;
        this.totalMonthlyRevenue = totalMonthlyRevenue != null ? totalMonthlyRevenue : BigDecimal.ZERO;
        this.mealTypeBreakdown = mealTypeBreakdown;
    }

    @Override
    public String generateBody() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("""
                OPERATIONAL KPI OVERVIEW:
                Date                   : %s
                Total Enrolled Students: %d
                Today Meals Served     : %d
                Average Food Rating    : %.2f / 5.00
                Open Complaints        : %d
                Total Monthly Revenue  : ₹%.2f
                
                MEAL-WISE TODAY DISTRIBUTION:
                ------------------------------------------------------------
                """,
                periodDate, totalEnrolledStudents, todayMealsServed,
                averageFoodRating, openComplaintsCount, totalMonthlyRevenue.doubleValue()));

        if (mealTypeBreakdown != null) {
            for (Map.Entry<String, Integer> entry : mealTypeBreakdown.entrySet()) {
                sb.append(String.format("  • %-12s: %d meals\n", entry.getKey(), entry.getValue()));
            }
        }
        return sb.toString();
    }

    @Override
    public String getMetricsSummary() {
        return String.format("Date: %s | Students: %d | Today Meals: %d | Avg Rating: %.2f | Revenue: ₹%.2f",
                periodDate, totalEnrolledStudents, todayMealsServed, averageFoodRating, totalMonthlyRevenue.doubleValue());
    }

    @Override
    public String toCsvRow() {
        return String.format("\"%s\",%d,%d,%.2f,%d,%.2f",
                periodDate, totalEnrolledStudents, todayMealsServed,
                averageFoodRating, openComplaintsCount, totalMonthlyRevenue.doubleValue());
    }

    @Override
    public String getCsvHeader() {
        return "Date,TotalStudents,TodayMeals,AvgRating,OpenComplaints,TotalRevenue";
    }

    public LocalDate getPeriodDate() { return periodDate; }
    public int getTotalEnrolledStudents() { return totalEnrolledStudents; }
    public int getTodayMealsServed() { return todayMealsServed; }
    public double getAverageFoodRating() { return averageFoodRating; }
    public int getOpenComplaintsCount() { return openComplaintsCount; }
    public BigDecimal getTotalMonthlyRevenue() { return totalMonthlyRevenue; }
}
