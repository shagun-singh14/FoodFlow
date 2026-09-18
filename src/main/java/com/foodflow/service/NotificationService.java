package com.foodflow.service;

import com.foodflow.config.DatabaseManager;
import com.foodflow.exception.DatabaseException;
import com.foodflow.model.Notification;
import com.foodflow.util.LoggerUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.Vector;

/**
 * Service managing student notifications, dues alerts, and feast announcements.
 * Demonstrates:
 * - Collections: Vector for thread-safe synchronized notification history collection
 */
public class NotificationService {

    private final DatabaseManager dbManager;

    public NotificationService() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public Notification createNotification(Long studentId, String message) throws DatabaseException {
        String sql = "INSERT INTO notifications (student_id, message, notification_date, is_read) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Notification notif = new Notification(null, studentId, message, LocalDate.now(), false);

            ps.setLong(1, studentId);
            ps.setString(2, message);
            ps.setDate(3, Date.valueOf(notif.getNotificationDate()));
            ps.setBoolean(4, false);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    notif.setNotificationId(rs.getLong(1));
                }
            }
            LoggerUtil.info(NotificationService.class, "Sent notification to Student #" + studentId + ": " + message);
            return notif;
        } catch (SQLException e) {
            throw new DatabaseException("Error creating notification: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves notifications for a student into a Vector.
     * Demonstrates Vector usage as required by the Java syllabus.
     */
    public Vector<Notification> getNotificationsForStudent(Long studentId) throws DatabaseException {
        String sql = "SELECT * FROM notifications WHERE student_id = ? ORDER BY created_at DESC";
        Vector<Notification> vector = new Vector<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notification n = new Notification(
                            rs.getLong("notification_id"),
                            rs.getLong("student_id"),
                            rs.getString("message"),
                            rs.getDate("notification_date").toLocalDate(),
                            rs.getBoolean("is_read")
                    );
                    Timestamp created = rs.getTimestamp("created_at");
                    if (created != null) n.setCreatedAt(created.toLocalDateTime());
                    vector.add(n);
                }
            }
            return vector;
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching notifications: " + e.getMessage(), e);
        }
    }

    public void markAllAsRead(Long studentId) throws DatabaseException {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE student_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, studentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error marking notifications as read: " + e.getMessage(), e);
        }
    }

    public int getUnreadCount(Long studentId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE student_id = ? AND is_read = FALSE";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error counting unread notifications: " + e.getMessage(), e);
        }
    }
}
