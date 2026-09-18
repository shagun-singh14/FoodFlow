package com.foodflow.model;

import com.foodflow.model.enums.MealType;
import com.foodflow.model.interfaces.Exportable;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model representing a verified meal attendance record for a student.
 */
public class MealAttendance implements Exportable, Serializable {

    private static final long serialVersionUID = 1L;

    private Long attendanceId;
    private Long studentId;
    private String studentName;
    private String registrationNumber;
    private LocalDate attendanceDate;
    private MealType mealType;
    private String status;
    private LocalDateTime markedAt;

    public MealAttendance() {
        this.status = "PRESENT";
        this.markedAt = LocalDateTime.now();
    }

    public MealAttendance(Long attendanceId, Long studentId, LocalDate attendanceDate, MealType mealType, String status) {
        this();
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.attendanceDate = attendanceDate;
        this.mealType = mealType;
        this.status = status != null ? status : "PRESENT";
    }

    @Override
    public String toFormattedText() {
        return String.format("[%s] Student #%d (%s) - %s: %s at %s",
                attendanceDate, studentId, registrationNumber != null ? registrationNumber : "N/A",
                mealType.getLabel(), status, markedAt);
    }

    @Override
    public String toCsvRow() {
        return String.format("%d,%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                attendanceId != null ? attendanceId : 0L,
                studentId,
                registrationNumber != null ? registrationNumber : "",
                attendanceDate,
                mealType.name(),
                status,
                markedAt);
    }

    @Override
    public String getCsvHeader() {
        return "AttendanceId,StudentId,RegNo,Date,MealType,Status,MarkedAt";
    }

    // Getters and Setters
    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getMarkedAt() {
        return markedAt;
    }

    public void setMarkedAt(LocalDateTime markedAt) {
        this.markedAt = markedAt;
    }

    @Override
    public String toString() {
        return String.format("Attendance[ID=%d, Student=%d, Date=%s, Meal=%s, Status=%s]",
                attendanceId, studentId, attendanceDate, mealType, status);
    }
}
