package com.foodflow.service;

import com.foodflow.dao.AttendanceJDBCDAO;
import com.foodflow.dao.StudentJDBCDAO;
import com.foodflow.exception.*;
import com.foodflow.model.MealAttendance;
import com.foodflow.model.Student;
import com.foodflow.model.enums.MealType;
import com.foodflow.util.LoggerUtil;

import java.time.LocalDate;
import java.util.List;

/**
 * Service managing student meal check-ins with thread synchronization.
 * Demonstrates:
 * - Synchronization (synchronized method preventing concurrency race conditions)
 * - Custom Exception handling hierarchy
 * - Business rule validation
 */
public class AttendanceService {

    private final AttendanceJDBCDAO attendanceDAO;
    private final StudentJDBCDAO studentDAO;

    public AttendanceService() {
        this.attendanceDAO = new AttendanceJDBCDAO();
        this.studentDAO = new StudentJDBCDAO();
    }

    /**
     * =========================================================================
     * SYNCHRONIZATION REQUIREMENT & EXPLANATION
     * =========================================================================
     * In a high-traffic college mess, hundreds of students tap their RFID cards
     * or check-in simultaneously via turnstiles / mobile app during peak meal hours.
     *
     * WHY SYNCHRONIZATION IS NECESSARY:
     * 1. Without synchronization, two concurrent threads (e.g., rapid double-taps
     *    or simultaneous turnstiles) could execute the 'check duplicate' step at
     *    the exact same millisecond. Both would find no existing attendance record
     *    and both would proceed to insert, causing duplicate meal charges and
     *    inconsistent inventory accounting.
     * 2. The 'synchronized' monitor lock guarantees strict atomicity and mutual
     *    exclusion for checking and recording the attendance transaction.
     * =========================================================================
     */
    public synchronized MealAttendance markAttendanceSynchronized(Long studentId, LocalDate date, MealType mealType)
            throws FoodFlowException {

        // 1. Validate Date
        if (date.isAfter(LocalDate.now())) {
            throw new InvalidMealException("Cannot mark meal attendance for future date: " + date);
        }

        // 2. Validate Student Enrollment
        Student student = studentDAO.findById(studentId);
        if (student == null) {
            throw new StudentNotFoundException(studentId);
        }
        if (!student.isEnrolled()) {
            throw new StudentNotEnrolledException(studentId);
        }

        // 3. Atomically Record Attendance
        LoggerUtil.info(AttendanceService.class,
                String.format("[SYNCHRONIZED LOCK ACQUIRED] Processing attendance for Student #%d (%s) on %s (%s)",
                        studentId, student.getName(), date, mealType.getLabel()));

        return attendanceDAO.recordAttendance(studentId, date, mealType);
    }

    public boolean hasStudentAttended(Long studentId, LocalDate date, MealType mealType) throws DatabaseException {
        return attendanceDAO.hasAttended(studentId, date, mealType);
    }

    public List<MealAttendance> getStudentAttendanceHistory(Long studentId) throws DatabaseException {
        return attendanceDAO.findByStudent(studentId);
    }

    public List<MealAttendance> getAttendanceByDate(LocalDate date) throws DatabaseException {
        return attendanceDAO.findByDate(date);
    }

    public int getMonthlyMealCount(Long studentId, int year, int month) throws DatabaseException {
        return attendanceDAO.countMealsByStudentAndMonth(studentId, year, month);
    }
}
