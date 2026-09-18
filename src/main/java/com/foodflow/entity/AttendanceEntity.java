package com.foodflow.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA Entity mapping the 'meal_attendance' table.
 * Demonstrates Many-to-One relationship with StudentEntity.
 */
@Entity
@Table(name = "meal_attendance", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "attendance_date", "meal_type"})
})
public class AttendanceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long attendanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "meal_type", nullable = false, length = 20)
    private String mealType;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PRESENT";

    @Column(name = "marked_at", insertable = false, updatable = false)
    private LocalDateTime markedAt;

    public AttendanceEntity() {
    }

    public AttendanceEntity(StudentEntity student, LocalDate attendanceDate, String mealType, String status) {
        this.student = student;
        this.attendanceDate = attendanceDate;
        this.mealType = mealType;
        this.status = status;
    }

    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public StudentEntity getStudent() {
        return student;
    }

    public void setStudent(StudentEntity student) {
        this.student = student;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
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
}
