package com.foodflow;

import com.foodflow.model.Student;
import com.foodflow.model.enums.PaymentStatus;
import com.foodflow.report.ReportGenerator;
import com.foodflow.report.StudentMealReport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReportIOTest {

    private static ReportGenerator reportGenerator;

    @BeforeAll
    public static void setup() {
        reportGenerator = new ReportGenerator();
    }

    @Test
    public void testCharacterStreamTextExportAndRead() throws Exception {
        Student s = new Student(99L, "Test Student", "test@vit.ac.in", "9998887770", "hash", "salt",
                "24BCE9999", "B.Tech CSE", 2, "Block A", "A-101", true);

        StudentMealReport report = new StudentMealReport(s, "September 2026", List.of(), BigDecimal.valueOf(1200.0), PaymentStatus.PENDING, "Test Runner");

        // Character stream write (BufferedWriter)
        ReportGenerator.ReportSummary summary = reportGenerator.exportToTextFile(report, "UnitTest_Report");
        assertTrue(summary.isSuccess());
        assertTrue(summary.getFileSizeBytes() > 0);

        // Character stream read (BufferedReader)
        String content = reportGenerator.readReportFile(summary.getFileName());
        assertNotNull(content);
        assertTrue(content.contains("Test Student"));
        assertTrue(content.contains("24BCE9999"));
    }

    @Test
    public void testByteStreamExportAndRead() throws Exception {
        byte[] payload = "FOODFLOW_TEST_BYTE_STREAM_12345".getBytes();

        // Byte stream write (FileOutputStream)
        ReportGenerator.ReportSummary summary = reportGenerator.exportToBinaryBackup(payload, "UnitTest_Binary");
        assertTrue(summary.isSuccess());

        // Byte stream read (FileInputStream)
        byte[] readBack = reportGenerator.readBinaryBackup(summary.getFileName());
        assertArrayEquals(payload, readBack);
    }
}
