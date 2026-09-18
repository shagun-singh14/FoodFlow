package com.foodflow.model;

import com.foodflow.model.enums.ComplaintCategory;
import com.foodflow.model.enums.ComplaintStatus;
import com.foodflow.model.interfaces.Exportable;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model representing a formal grievance/complaint filed by a student.
 */
public class Complaint implements Exportable, Serializable {

    private static final long serialVersionUID = 1L;

    private Long complaintId;
    private Long studentId;
    private String studentName;
    private String registrationNumber;
    private ComplaintCategory category;
    private String description;
    private ComplaintStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private String adminRemarks;

    public Complaint() {
        this.status = ComplaintStatus.OPEN;
        this.createdAt = LocalDateTime.now();
    }

    public Complaint(Long complaintId, Long studentId, ComplaintCategory category, String description) {
        this();
        this.complaintId = complaintId;
        this.studentId = studentId;
        this.category = category != null ? category : ComplaintCategory.OTHER;
        this.description = description;
    }

    public void resolve(String remarks) {
        this.status = ComplaintStatus.RESOLVED;
        this.adminRemarks = remarks;
        this.resolvedAt = LocalDateTime.now();
    }

    public void markInProgress(String remarks) {
        this.status = ComplaintStatus.IN_PROGRESS;
        this.adminRemarks = remarks;
    }

    public void reject(String remarks) {
        this.status = ComplaintStatus.REJECTED;
        this.adminRemarks = remarks;
        this.resolvedAt = LocalDateTime.now();
    }

    @Override
    public String toFormattedText() {
        return String.format("Complaint #%d [%s] - Status: %s | Student: %s (%s)\nDescription: %s\nAdmin Remarks: %s\nFiled: %s | Resolved: %s\n",
                complaintId, category.getLabel(), status.getDisplay(),
                studentName != null ? studentName : "ID " + studentId,
                registrationNumber != null ? registrationNumber : "N/A",
                description, adminRemarks != null ? adminRemarks : "None",
                createdAt, resolvedAt != null ? resolvedAt.toString() : "Pending");
    }

    @Override
    public String toCsvRow() {
        return String.format("%d,%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                complaintId != null ? complaintId : 0L,
                studentId,
                registrationNumber != null ? registrationNumber : "",
                category.name(),
                description != null ? description.replace("\"", "\"\"") : "",
                status.name(),
                adminRemarks != null ? adminRemarks.replace("\"", "\"\"") : "",
                createdAt,
                resolvedAt != null ? resolvedAt.toString() : "");
    }

    @Override
    public String getCsvHeader() {
        return "ComplaintId,StudentId,RegNo,Category,Description,Status,AdminRemarks,CreatedAt,ResolvedAt";
    }

    // Getters and Setters
    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public ComplaintCategory getCategory() {
        return category;
    }

    public void setCategory(ComplaintCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getAdminRemarks() {
        return adminRemarks;
    }

    public void setAdminRemarks(String adminRemarks) {
        this.adminRemarks = adminRemarks;
    }

    @Override
    public String toString() {
        return String.format("Complaint[ID=%d, Student=%d, Cat=%s, Status=%s]", complaintId, studentId, category, status);
    }
}
