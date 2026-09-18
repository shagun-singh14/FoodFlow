package com.foodflow.exception;

import com.foodflow.model.enums.MealType;
import java.time.LocalDate;

/**
 * Exception thrown when an attendance record already exists for a student on a given date and meal session.
 */
public class DuplicateMealAttendanceException extends FoodFlowException {

    private final Long studentId;
    private final LocalDate date;
    private final MealType mealType;

    public DuplicateMealAttendanceException(Long studentId, LocalDate date, MealType mealType) {
        super(String.format("Duplicate attendance: Student ID %d has already marked %s on %s.", studentId, mealType.getLabel(), date),
                "ERR_DUPLICATE_ATTENDANCE");
        this.studentId = studentId;
        this.date = date;
        this.mealType = mealType;
    }

    public Long getStudentId() {
        return studentId;
    }

    public LocalDate getDate() {
        return date;
    }

    public MealType getMealType() {
        return mealType;
    }
}
