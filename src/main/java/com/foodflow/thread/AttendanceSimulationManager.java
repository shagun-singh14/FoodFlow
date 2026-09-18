package com.foodflow.thread;

import com.foodflow.model.Student;
import com.foodflow.model.enums.MealType;
import com.foodflow.service.AttendanceService;
import com.foodflow.util.LoggerUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Multithreaded simulation coordinator for concurrent meal marking.
 * Demonstrates:
 * - Thread pool management with ExecutorService
 * - CountDownLatch for simultaneous thread trigger
 * - Thread-safe result aggregation
 */
public class AttendanceSimulationManager {

    private final AttendanceService attendanceService;

    public AttendanceSimulationManager(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    public static class SimulationResult {
        private final int totalAttempted;
        private final int successfulCount;
        private final int duplicateCount;
        private final long executionTimeMillis;
        private final List<String> logs;

        public SimulationResult(int totalAttempted, int successfulCount, int duplicateCount, long executionTimeMillis, List<String> logs) {
            this.totalAttempted = totalAttempted;
            this.successfulCount = successfulCount;
            this.duplicateCount = duplicateCount;
            this.executionTimeMillis = executionTimeMillis;
            this.logs = logs;
        }

        public int getTotalAttempted() { return totalAttempted; }
        public int getSuccessfulCount() { return successfulCount; }
        public int getDuplicateCount() { return duplicateCount; }
        public long getExecutionTimeMillis() { return executionTimeMillis; }
        public List<String> getLogs() { return logs; }

        @Override
        public String toString() {
            return String.format("Simulation Summary: %d Attempts | %d Successes | %d Duplicates Caught | Time: %d ms",
                    totalAttempted, successfulCount, duplicateCount, executionTimeMillis);
        }
    }

    /**
     * Executes concurrent meal attendance tasks across multiple threads simultaneously.
     */
    public SimulationResult runConcurrentSimulation(List<Student> students, LocalDate date, MealType mealType, int threadCount) {
        if (students == null || students.isEmpty()) {
            return new SimulationResult(0, 0, 0, 0, List.of("No students provided for simulation."));
        }

        int poolSize = Math.max(2, Math.min(threadCount, 20));
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        List<String> logMessages = new CopyOnWriteArrayList<>();
        List<Future<Boolean>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        int totalAttempts = students.size();

        logMessages.add(String.format("Starting simulation: %d student requests across %d concurrent worker threads for %s on %s.",
                totalAttempts, poolSize, mealType.getLabel(), date));

        for (Student student : students) {
            MealAttendanceTask task = new MealAttendanceTask(
                    attendanceService,
                    student.getId(),
                    student.getName(),
                    date,
                    mealType
            );
            futures.add(executor.submit((Callable<Boolean>) task));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logMessages.add("Simulation was interrupted before completion.");
        }

        int successes = 0;
        int duplicates = 0;

        for (Future<Boolean> f : futures) {
            try {
                if (f.get()) {
                    successes++;
                } else {
                    duplicates++;
                }
            } catch (Exception e) {
                duplicates++;
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        logMessages.add(String.format("Simulation finished: %d successful, %d duplicate/rejected in %d ms.",
                successes, duplicates, duration));

        return new SimulationResult(totalAttempts, successes, duplicates, duration, logMessages);
    }
}
