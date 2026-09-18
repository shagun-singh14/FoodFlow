package com.foodflow.service;

import com.foodflow.dao.AttendanceJDBCDAO;
import com.foodflow.dao.StudentJDBCDAO;
import com.foodflow.exception.DatabaseException;
import com.foodflow.model.MealAttendance;
import com.foodflow.model.Student;
import com.foodflow.model.enums.MealType;

import java.time.LocalDate;
import java.util.List;

/**
 * Analytical service processing high-throughput arrays for operational dashboards.
 * Demonstrates:
 * - 1-D Array: Daily meal session attendance distribution [Breakfast, Lunch, Snacks, Dinner]
 * - 2-D Array: Matrix grid representing Student Index × Meal Type Attendance (Students rows, 4 Meal columns)
 * - Jagged Array: Multi-month attendance trends where each month has variable recorded days (e.g. 28, 30, 31 days)
 */
public class AnalyticsService {

    private final AttendanceJDBCDAO attendanceDAO;
    private final StudentJDBCDAO studentDAO;

    public AnalyticsService() {
        this.attendanceDAO = new AttendanceJDBCDAO();
        this.studentDAO = new StudentJDBCDAO();
    }

    // =========================================================================
    // 1-D ARRAY REQUIREMENT: Daily Meal Statistics
    // Index 0 = Breakfast, 1 = Lunch, 2 = Snacks, 3 = Dinner
    // =========================================================================
    public int[] calculateDailyMealStatistics(LocalDate date) throws DatabaseException {
        int[] mealCounts = new int[4]; // 1-D Array of fixed size 4

        List<MealAttendance> list = attendanceDAO.findByDate(date);
        for (MealAttendance a : list) {
            switch (a.getMealType()) {
                case BREAKFAST -> mealCounts[0]++;
                case LUNCH     -> mealCounts[1]++;
                case SNACKS    -> mealCounts[2]++;
                case DINNER    -> mealCounts[3]++;
            }
        }
        return mealCounts;
    }

    // =========================================================================
    // 2-D ARRAY REQUIREMENT: Student × Meal Type Attendance Grid
    // Matrix Dimensions: [N students] × [4 meal types]
    // Cell value: 1 if student attended that meal, 0 otherwise
    // =========================================================================
    public int[][] generateStudentMealMatrix(List<Student> students, LocalDate date) throws DatabaseException {
        if (students == null || students.isEmpty()) {
            return new int[0][4];
        }

        int[][] matrix = new int[students.size()][4]; // 2-D Array
        List<MealAttendance> dayAttendance = attendanceDAO.findByDate(date);

        for (int i = 0; i < students.size(); i++) {
            Long studentId = students.get(i).getId();
            for (MealAttendance a : dayAttendance) {
                if (a.getStudentId().equals(studentId)) {
                    int mealIndex = switch (a.getMealType()) {
                        case BREAKFAST -> 0;
                        case LUNCH     -> 1;
                        case SNACKS    -> 2;
                        case DINNER    -> 3;
                    };
                    matrix[i][mealIndex] = 1; // Mark Present in 2D array
                }
            }
        }
        return matrix;
    }

    // =========================================================================
    // JAGGED ARRAY REQUIREMENT: Multi-Month Variable Days Attendance Trends
    // Row 0 = January (31 days), Row 1 = February (28 days), Row 2 = March (31 days), etc.
    // Demonstrates Jagged Arrays with unequal sub-array lengths.
    // =========================================================================
    public int[][] generateMonthlyJaggedAttendanceTrend(Long studentId, int year) throws DatabaseException {
        // Jagged array: 12 months with varying days in each month
        int[][] jaggedMonthlyStats = new int[12][];

        int[] daysInMonths = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        // Check for leap year
        if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) {
            daysInMonths[1] = 29;
        }

        // Allocate jagged rows with unequal lengths
        for (int m = 0; m < 12; m++) {
            jaggedMonthlyStats[m] = new int[daysInMonths[m]];
        }

        // Populate from student attendance records
        List<MealAttendance> history = attendanceDAO.findByStudent(studentId);
        for (MealAttendance a : history) {
            LocalDate d = a.getAttendanceDate();
            if (d.getYear() == year) {
                int monthIdx = d.getMonthValue() - 1;
                int dayIdx = d.getDayOfMonth() - 1;
                if (monthIdx >= 0 && monthIdx < 12 && dayIdx < jaggedMonthlyStats[monthIdx].length) {
                    jaggedMonthlyStats[monthIdx][dayIdx]++; // Increment meal count on that day
                }
            }
        }

        return jaggedMonthlyStats;
    }

    /**
     * Helper to compute total meals across a jagged array.
     */
    public int sumJaggedArrayMeals(int[][] jaggedArray) {
        int total = 0;
        for (int[] monthRow : jaggedArray) {
            for (int dayMeals : monthRow) {
                total += dayMeals;
            }
        }
        return total;
    }
}
