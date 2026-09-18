package com.foodflow.thread;

import com.foodflow.exception.DuplicateMealAttendanceException;
import com.foodflow.exception.FoodFlowException;
import com.foodflow.model.enums.MealType;
import com.foodflow.service.AttendanceService;
import com.foodflow.util.LoggerUtil;

import java.time.LocalDate;
import java.util.concurrent.Callable;

/**
 * Multithreaded Task simulating a single student marking meal attendance concurrently.
 * Demonstrates:
 * - Multithreading via java.lang.Runnable and Callable<Boolean>
 * - Thread concurrency handling
 * - Integration with synchronized business service layer
 */
public class MealAttendanceTask implements Runnable, Callable<Boolean> {

    private final AttendanceService attendanceService;
    private final Long studentId;
    private final String studentName;
    private final LocalDate attendanceDate;
    private final MealType mealType;
    private boolean success = false;
    private String resultMessage;

    public MealAttendanceTask(AttendanceService attendanceService, Long studentId, String studentName,
                              LocalDate attendanceDate, MealType mealType) {
        this.attendanceService = attendanceService;
        this.studentId = studentId;
        this.studentName = studentName;
        this.attendanceDate = attendanceDate;
        this.mealType = mealType;
    }

    @Override
    public void run() {
        call();
    }

    @Override
    public Boolean call() {
        String threadName = Thread.currentThread().getName();
        try {
            LoggerUtil.info(MealAttendanceTask.class,
                    String.format("[%s] Student '%s' (ID: %d) attempting to mark %s on %s...",
                            threadName, studentName, studentId, mealType.getLabel(), attendanceDate));

            // Call the synchronized attendance service method
            attendanceService.markAttendanceSynchronized(studentId, attendanceDate, mealType);

            this.success = true;
            this.resultMessage = String.format("SUCCESS: Attendance verified for %s [%s]", studentName, mealType.getLabel());
            LoggerUtil.info(MealAttendanceTask.class, "[" + threadName + "] " + resultMessage);
        } catch (DuplicateMealAttendanceException e) {
            this.success = false;
            this.resultMessage = String.format("REJECTED: Duplicate check caught for %s - %s", studentName, e.getMessage());
            LoggerUtil.warning(MealAttendanceTask.class, "[" + threadName + "] " + resultMessage);
        } catch (FoodFlowException e) {
            this.success = false;
            this.resultMessage = String.format("FAILED: Validation/enrollment error for %s: %s", studentName, e.getMessage());
            LoggerUtil.warning(MealAttendanceTask.class, "[" + threadName + "] " + resultMessage);
        } catch (Exception e) {
            this.success = false;
            this.resultMessage = String.format("ERROR: Unexpected concurrency failure: %s", e.getMessage());
            LoggerUtil.severe(MealAttendanceTask.class, "[" + threadName + "] " + resultMessage, e);
        }
        return this.success;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }
}
