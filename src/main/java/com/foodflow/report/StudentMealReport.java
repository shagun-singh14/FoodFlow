package com.foodflow.report;

import com.foodflow.model.MealAttendance;
import com.foodflow.model.Student;
import com.foodflow.model.enums.MealType;
import com.foodflow.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;

/**
 * Concrete report generating detailed monthly meal consumption and invoice summary for a specific student.
 * Demonstrates:
 * - Inheritance (extends Report)
 * - Method Overriding (generateBody, getMetricsSummary, toCsvRow, getCsvHeader)
 * - Polymorphic invocation
 */
public class StudentMealReport extends Report {

    private final Student student;
    private final String month;
    private final List<MealAttendance> attendanceRecords;
    private int breakfastCount;
    private int lunchCount;
    private int snacksCount;
    private int dinnerCount;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;

    public StudentMealReport(Student student, String month, List<MealAttendance> attendanceRecords,
                             BigDecimal totalAmount, PaymentStatus paymentStatus, String generatedBy) {
        super("Student Monthly Meal & Billing Report - " + month, generatedBy);
        this.student = student;
        this.month = month;
        this.attendanceRecords = attendanceRecords;
        this.totalAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
        this.paymentStatus = paymentStatus != null ? paymentStatus : PaymentStatus.PENDING;

        calculateMealBreakdown();
    }

    private void calculateMealBreakdown() {
        this.breakfastCount = 0;
        this.lunchCount = 0;
        this.snacksCount = 0;
        this.dinnerCount = 0;

        if (attendanceRecords != null) {
            for (MealAttendance a : attendanceRecords) {
                if (a.getMealType() == MealType.BREAKFAST) breakfastCount++;
                else if (a.getMealType() == MealType.LUNCH) lunchCount++;
                else if (a.getMealType() == MealType.SNACKS) snacksCount++;
                else if (a.getMealType() == MealType.DINNER) dinnerCount++;
            }
        }
    }

    @Override
    public String generateBody() {
        int totalMeals = breakfastCount + lunchCount + snacksCount + dinnerCount;
        return String.format("""
                STUDENT DETAILS:
                Name       : %s
                Reg Number : %s
                Course/Year: %s (Year %d)
                Hostel/Room: %s (%s)
                
                MEAL CONSUMPTION BREAKDOWN (%s):
                ------------------------------------------------------------
                Breakfast  : %2d meals @ ₹35.00 = ₹%.2f
                Lunch      : %2d meals @ ₹50.00 = ₹%.2f
                Snacks     : %2d meals @ ₹25.00 = ₹%.2f
                Dinner     : %2d meals @ ₹45.00 = ₹%.2f
                ------------------------------------------------------------
                Total Meals: %2d meals
                Total Bill : ₹%.2f
                Status     : %s
                """,
                student.getName(), student.getRegistrationNumber(), student.getCourse(), student.getYear(),
                student.getHostel(), student.getRoomNumber(),
                month,
                breakfastCount, (breakfastCount * 35.0),
                lunchCount, (lunchCount * 50.0),
                snacksCount, (snacksCount * 25.0),
                dinnerCount, (dinnerCount * 45.0),
                totalMeals, totalAmount.doubleValue(), paymentStatus.getLabel());
    }

    @Override
    public String getMetricsSummary() {
        int totalMeals = breakfastCount + lunchCount + snacksCount + dinnerCount;
        return String.format("Student: %s | Month: %s | Meals: %d | Amount: ₹%.2f | Status: %s",
                student.getRegistrationNumber(), month, totalMeals, totalAmount.doubleValue(), paymentStatus.getLabel());
    }

    @Override
    public String toCsvRow() {
        int totalMeals = breakfastCount + lunchCount + snacksCount + dinnerCount;
        return String.format("\"%s\",\"%s\",\"%s\",%d,%d,%d,%d,%d,%.2f,\"%s\"",
                student.getRegistrationNumber(), student.getName(), month,
                breakfastCount, lunchCount, snacksCount, dinnerCount, totalMeals,
                totalAmount.doubleValue(), paymentStatus.name());
    }

    @Override
    public String getCsvHeader() {
        return "RegistrationNumber,StudentName,Month,Breakfast,Lunch,Snacks,Dinner,TotalMeals,Amount,PaymentStatus";
    }

    public Student getStudent() { return student; }
    public String getMonth() { return month; }
    public int getBreakfastCount() { return breakfastCount; }
    public int getLunchCount() { return lunchCount; }
    public int getSnacksCount() { return snacksCount; }
    public int getDinnerCount() { return dinnerCount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
}
