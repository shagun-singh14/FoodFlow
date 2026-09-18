package com.foodflow.report;

import com.foodflow.config.AppConfig;
import com.foodflow.exception.FoodFlowException;
import com.foodflow.model.interfaces.Exportable;
import com.foodflow.util.LoggerUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Report Generator and File I/O subsystem.
 * Demonstrates:
 * - Nested Inner Class (ReportSummary)
 * - Character-oriented streams: FileWriter, FileReader, BufferedReader, BufferedWriter
 * - Byte-oriented streams: FileOutputStream, FileInputStream
 * - Java I/O Exception handling & resource management (try-with-resources)
 */
public class ReportGenerator {

    /**
     * Inner Class: ReportSummary
     * Encapsulates the metadata and file export outcome.
     * Demonstrates Non-static Inner Class in Java.
     */
    public class ReportSummary {
        private final String fileName;
        private final String format;
        private final long fileSizeBytes;
        private final LocalDateTime timestamp;
        private final boolean isSuccess;
        private final String message;

        public ReportSummary(String fileName, String format, long fileSizeBytes, boolean isSuccess, String message) {
            this.fileName = fileName;
            this.format = format;
            this.fileSizeBytes = fileSizeBytes;
            this.timestamp = LocalDateTime.now();
            this.isSuccess = isSuccess;
            this.message = message;
        }

        public String getFileName() { return fileName; }
        public String getFormat() { return format; }
        public long getFileSizeBytes() { return fileSizeBytes; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public boolean isSuccess() { return isSuccess; }
        public String getMessage() { return message; }

        @Override
        public String toString() {
            return String.format("ReportSummary [File='%s', Format='%s', Size=%d bytes, Status=%s]",
                    fileName, format, fileSizeBytes, isSuccess ? "OK" : "FAILED");
        }
    }

    private final File reportsDir;

    public ReportGenerator() {
        this.reportsDir = new File(AppConfig.getReportsExportDir());
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }
    }

    /**
     * Exports a Report to a formatted .txt text file using Character Streams (BufferedWriter & FileWriter).
     */
    public ReportSummary exportToTextFile(Report report, String baseFileName) throws FoodFlowException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = baseFileName + "_" + timestamp + ".txt";
        File targetFile = new File(reportsDir, fileName);

        try (FileWriter fw = new FileWriter(targetFile, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write(report.toFormattedText());
            bw.flush();

            LoggerUtil.info(ReportGenerator.class, "Exported text report using BufferedWriter: " + targetFile.getAbsolutePath());
            return new ReportSummary(fileName, "TXT", targetFile.length(), true, "Exported successfully to " + targetFile.getPath());
        } catch (IOException e) {
            LoggerUtil.severe(ReportGenerator.class, "Error exporting text report", e);
            throw new FoodFlowException("Failed to export text report: " + e.getMessage(), "ERR_IO_WRITE", e);
        }
    }

    /**
     * Exports a list of Exportable items to a .csv file using Character Streams (BufferedWriter & FileWriter).
     */
    public ReportSummary exportToCsvFile(List<? extends Exportable> items, String baseFileName) throws FoodFlowException {
        if (items == null || items.isEmpty()) {
            throw new FoodFlowException("No items available to export to CSV.", "ERR_NO_DATA");
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = baseFileName + "_" + timestamp + ".csv";
        File targetFile = new File(reportsDir, fileName);

        try (FileWriter fw = new FileWriter(targetFile, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(fw)) {

            // Write CSV Header
            bw.write(items.get(0).getCsvHeader());
            bw.newLine();

            // Write CSV Rows
            for (Exportable item : items) {
                bw.write(item.toCsvRow());
                bw.newLine();
            }
            bw.flush();

            LoggerUtil.info(ReportGenerator.class, "Exported CSV using BufferedWriter: " + targetFile.getAbsolutePath());
            return new ReportSummary(fileName, "CSV", targetFile.length(), true, "Exported successfully to " + targetFile.getPath());
        } catch (IOException e) {
            LoggerUtil.severe(ReportGenerator.class, "Error exporting CSV file", e);
            throw new FoodFlowException("Failed to export CSV file: " + e.getMessage(), "ERR_IO_WRITE", e);
        }
    }

    /**
     * Demonstrates Byte-oriented Stream export using FileOutputStream.
     */
    public ReportSummary exportToBinaryBackup(byte[] data, String baseFileName) throws FoodFlowException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = baseFileName + "_" + timestamp + ".dat";
        File targetFile = new File(reportsDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(targetFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            bos.write(data);
            bos.flush();

            LoggerUtil.info(ReportGenerator.class, "Exported binary backup using FileOutputStream: " + targetFile.getAbsolutePath());
            return new ReportSummary(fileName, "BINARY", targetFile.length(), true, "Binary backup saved: " + targetFile.getPath());
        } catch (IOException e) {
            LoggerUtil.severe(ReportGenerator.class, "Error exporting binary backup", e);
            throw new FoodFlowException("Failed to export binary backup: " + e.getMessage(), "ERR_IO_BYTE_WRITE", e);
        }
    }

    /**
     * Reads a report file back into a String using Character Streams (FileReader & BufferedReader).
     */
    public String readReportFile(String fileName) throws FoodFlowException {
        File file = new File(reportsDir, fileName);
        if (!file.exists()) {
            throw new FoodFlowException("Report file '" + fileName + "' not found.", "ERR_FILE_NOT_FOUND");
        }

        StringBuilder content = new StringBuilder();
        try (FileReader fr = new FileReader(file, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(fr)) {

            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            LoggerUtil.severe(ReportGenerator.class, "Error reading report file", e);
            throw new FoodFlowException("Failed to read report file: " + e.getMessage(), "ERR_IO_READ", e);
        }
    }

    /**
     * Reads binary data back using Byte-oriented Streams (FileInputStream).
     */
    public byte[] readBinaryBackup(String fileName) throws FoodFlowException {
        File file = new File(reportsDir, fileName);
        if (!file.exists()) {
            throw new FoodFlowException("Backup file '" + fileName + "' not found.", "ERR_FILE_NOT_FOUND");
        }

        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            LoggerUtil.severe(ReportGenerator.class, "Error reading binary backup", e);
            throw new FoodFlowException("Failed to read binary backup: " + e.getMessage(), "ERR_IO_BYTE_READ", e);
        }
    }
}
