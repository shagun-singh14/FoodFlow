package com.foodflow;

import com.foodflow.model.Student;
import com.foodflow.service.AnalyticsService;
import com.foodflow.service.StudentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ArrayAnalyticsTest {

    private static AnalyticsService analyticsService;
    private static StudentService studentService;

    @BeforeAll
    public static void setup() {
        analyticsService = new AnalyticsService();
        studentService = new StudentService();
    }

    @Test
    public void test1DArrayDailyStats() throws Exception {
        int[] stats = analyticsService.calculateDailyMealStatistics(LocalDate.now());
        assertNotNull(stats);
        assertEquals(4, stats.length, "1-D array must have size 4 (Breakfast, Lunch, Snacks, Dinner)");
    }

    @Test
    public void test2DArrayStudentMealMatrix() throws Exception {
        List<Student> students = studentService.getAllStudents();
        int[][] matrix = analyticsService.generateStudentMealMatrix(students, LocalDate.now());
        assertNotNull(matrix);
        assertEquals(students.size(), matrix.length, "2-D array rows must equal student count");
        assertEquals(4, matrix[0].length, "2-D array columns must equal 4 meal types");
    }

    @Test
    public void testJaggedArrayMonthlyStats() throws Exception {
        int[][] jagged = analyticsService.generateMonthlyJaggedAttendanceTrend(3L, 2026);
        assertNotNull(jagged);
        assertEquals(12, jagged.length, "Jagged array must have 12 rows for 12 months");
        assertEquals(31, jagged[0].length, "January must have 31 days");
        assertEquals(28, jagged[1].length, "February (non-leap) must have 28 days");
        assertEquals(30, jagged[3].length, "April must have 30 days");

        int sum = analyticsService.sumJaggedArrayMeals(jagged);
        assertTrue(sum >= 0);
    }
}
