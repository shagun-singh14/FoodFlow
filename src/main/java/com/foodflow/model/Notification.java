package com.foodflow.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model representing student notifications, dues alerts, and menu updates.
 */
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long notificationId;
    private Long studentId;
    private String message;
    private LocalDate notificationDate;
    private boolean isRead;
    private LocalDateTime createdAt;

    public Notification() {
        this.notificationDate = LocalDate.now();
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public Notification(Long notificationId, Long studentId, String message, LocalDate notificationDate, boolean isRead) {
        this();
        this.notificationId = notificationId;
        this.studentId = studentId;
        this.message = message;
        this.notificationDate = notificationDate != null ? notificationDate : LocalDate.now();
        this.isRead = isRead;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    // Getters and Setters
    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(LocalDate notificationDate) {
        this.notificationDate = notificationDate;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (Read: %s)", notificationDate, message, isRead ? "Yes" : "No");
    }
}
