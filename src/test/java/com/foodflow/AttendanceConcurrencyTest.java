package com.foodflow;

import com.foodflow.exception.DuplicateMealAttendanceException;
import com.foodflow.model.Student;
import com.foodflow.model.enums.MealType;
import com.foodflow.service.AttendanceService;
import com.foodflow.service.StudentService;
import com.foodflow.thread.AttendanceSimulationManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AttendanceConcurrencyTest {

    private static AttendanceService attendanceService;
    private static StudentService studentService;

    @BeforeAll
    public static void setup() {
        attendanceService = new AttendanceService();
        studentService = new StudentService();
    }

    @Test
    public void testDuplicateAttendanceThrowsException() throws Exception {
        LocalDate testDate = LocalDate.now().minusDays(1);
        MealType testMeal = MealType.SNACKS;
        Long testStudentId = 4L;

        // Try marking twice
        try {
            attendanceService.markAttendanceSynchronized(testStudentId, testDate, testMeal);
        } catch (DuplicateMealAttendanceException ignored) {
            // Already marked in prior run, fine
        }

        // Second time must throw DuplicateMealAttendanceException
        assertThrows(DuplicateMealAttendanceException.class, () -> {
            attendanceService.markAttendanceSynchronized(testStudentId, testDate, testMeal);
        });
    }

    @Test
    public void testConcurrentSimulationThreadSafety() throws Exception {
        List<Student> students = studentService.getAllStudents();
        AttendanceSimulationManager simManager = new AttendanceSimulationManager(attendanceService);

        LocalDate date = LocalDate.now().minusDays(2);
        AttendanceSimulationManager.SimulationResult result =
                simManager.runConcurrentSimulation(students, date, MealType.LUNCH, 4);

        assertNotNull(result);
        assertEquals(students.size(), result.getTotalAttempted());
        assertTrue(result.getSuccessfulCount() + result.getDuplicateCount() == result.getTotalAttempted());
    }
}
