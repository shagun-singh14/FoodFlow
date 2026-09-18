package com.foodflow;

import org.junit.jupiter.api.Assertions;

/**
 * Direct Test Harness executing all test suites programmatically.
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   FOODFLOW JUNIT 5 TEST EXECUTION HARNESS                                      ");
        System.out.println("================================================================================\n");

        int totalPassed = 0;
        int totalFailed = 0;

        // 1. AuthServiceTest
        try {
            AuthServiceTest.setup();
            AuthServiceTest authTest = new AuthServiceTest();
            authTest.testStudentLoginSuccess();
            authTest.testAdminLoginSuccess();
            authTest.testLoginInvalidPasswordThrowsAuthenticationException();
            authTest.testStudentRegistrationSuccess();
            System.out.println("  ✓ [PASS] AuthServiceTest (4 tests)");
            totalPassed += 4;
        } catch (Throwable t) {
            System.err.println("  ✗ [FAIL] AuthServiceTest: " + t.getMessage());
            totalFailed++;
        }

        // 2. StudentServiceTest
        try {
            StudentServiceTest.setup();
            StudentServiceTest stuTest = new StudentServiceTest();
            stuTest.testGetAllStudents();
            stuTest.testMethodOverloadingSearchByName();
            stuTest.testMethodOverloadingSearchById();
            stuTest.testMethodOverloadingSearchByCombo();
            System.out.println("  ✓ [PASS] StudentServiceTest (4 tests - Method Overloading)");
            totalPassed += 4;
        } catch (Throwable t) {
            System.err.println("  ✗ [FAIL] StudentServiceTest: " + t.getMessage());
            totalFailed++;
        }

        // 3. MenuServiceTest
        try {
            MenuServiceTest.setup();
            MenuServiceTest menuTest = new MenuServiceTest();
            menuTest.testGetTodayMenu();
            menuTest.testMenuCrudAndStackUndo();
            System.out.println("  ✓ [PASS] MenuServiceTest (2 tests - Stack Undo)");
            totalPassed += 2;
        } catch (Throwable t) {
            System.err.println("  ✗ [FAIL] MenuServiceTest: " + t.getMessage());
            totalFailed++;
        }

        // 4. AttendanceConcurrencyTest
        try {
            AttendanceConcurrencyTest.setup();
            AttendanceConcurrencyTest attTest = new AttendanceConcurrencyTest();
            attTest.testDuplicateAttendanceThrowsException();
            attTest.testConcurrentSimulationThreadSafety();
            System.out.println("  ✓ [PASS] AttendanceConcurrencyTest (2 tests - Multithreading & Synchronization)");
            totalPassed += 2;
        } catch (Throwable t) {
            System.err.println("  ✗ [FAIL] AttendanceConcurrencyTest: " + t.getMessage());
            totalFailed++;
        }

        // 5. ComplaintServiceTest
        try {
            ComplaintServiceTest.setup();
            ComplaintServiceTest compTest = new ComplaintServiceTest();
            compTest.testFileAndResolveComplaint();
            System.out.println("  ✓ [PASS] ComplaintServiceTest (1 test)");
            totalPassed += 1;
        } catch (Throwable t) {
            System.err.println("  ✗ [FAIL] ComplaintServiceTest: " + t.getMessage());
            totalFailed++;
        }

        // 6. BillingServiceTest
        try {
            BillingServiceTest.setup();
            BillingServiceTest billTest = new BillingServiceTest();
            billTest.testGenerateMonthlyBill();
            billTest.testTotalRevenueCalculation();
            System.out.println("  ✓ [PASS] BillingServiceTest (2 tests)");
            totalPassed += 2;
        } catch (Throwable t) {
            System.err.println("  ✗ [FAIL] BillingServiceTest: " + t.getMessage());
            totalFailed++;
        }

        // 7. ReportIOTest
        try {
            ReportIOTest.setup();
            ReportIOTest ioTest = new ReportIOTest();
            ioTest.testCharacterStreamTextExportAndRead();
            ioTest.testByteStreamExportAndRead();
            System.out.println("  ✓ [PASS] ReportIOTest (2 tests - Char & Byte Streams)");
            totalPassed += 2;
        } catch (Throwable t) {
            System.err.println("  ✗ [FAIL] ReportIOTest: " + t.getMessage());
            totalFailed++;
        }

        // 8. ArrayAnalyticsTest
        try {
            ArrayAnalyticsTest.setup();
            ArrayAnalyticsTest arrTest = new ArrayAnalyticsTest();
            arrTest.test1DArrayDailyStats();
            arrTest.test2DArrayStudentMealMatrix();
            arrTest.testJaggedArrayMonthlyStats();
            System.out.println("  ✓ [PASS] ArrayAnalyticsTest (3 tests - 1D, 2D & Jagged Arrays)");
            totalPassed += 3;
        } catch (Throwable t) {
            System.err.println("  ✗ [FAIL] ArrayAnalyticsTest: " + t.getMessage());
            totalFailed++;
        }

        // 9. ReflectionInspectorTest
        try {
            ReflectionInspectorTest refTest = new ReflectionInspectorTest();
            refTest.testInspectStudentClassHierarchyAndMetadata();
            System.out.println("  ✓ [PASS] ReflectionInspectorTest (1 test - Java Reflection)");
            totalPassed += 1;
        } catch (Throwable t) {
            System.err.println("  ✗ [FAIL] ReflectionInspectorTest: " + t.getMessage());
            totalFailed++;
        }

        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println(" TEST RESULTS: " + totalPassed + " PASSED | " + totalFailed + " FAILED");
        System.out.println(" STATUS      : " + (totalFailed == 0 ? "ALL TESTS PASSED SUCCESSFULLY! (100%)" : "FAILURES OCCURRED"));
        System.out.println("--------------------------------------------------------------------------------\n");
    }
}
