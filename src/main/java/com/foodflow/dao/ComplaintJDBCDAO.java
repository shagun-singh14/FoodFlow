package com.foodflow.dao;

import com.foodflow.config.DatabaseManager;
import com.foodflow.exception.ComplaintNotFoundException;
import com.foodflow.exception.DatabaseException;
import com.foodflow.model.Complaint;
import com.foodflow.model.enums.ComplaintCategory;
import com.foodflow.model.enums.ComplaintStatus;
import com.foodflow.util.LoggerUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Direct JDBC DAO for Complaint management and grievance resolution workflows.
 */
public class ComplaintJDBCDAO {

    private final DatabaseManager dbManager;

    public ComplaintJDBCDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public Complaint createComplaint(Complaint complaint) throws DatabaseException {
        String sql = """
                INSERT INTO complaints (student_id, category, description, status, admin_remarks)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, complaint.getStudentId());
            ps.setString(2, complaint.getCategory().name());
            ps.setString(3, complaint.getDescription());
            ps.setString(4, complaint.getStatus().name());
            ps.setString(5, complaint.getAdminRemarks());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    complaint.setComplaintId(rs.getLong(1));
                }
            }
            LoggerUtil.info(ComplaintJDBCDAO.class, "Filed Complaint #" + complaint.getComplaintId() + " for student ID " + complaint.getStudentId());
            return complaint;
        } catch (SQLException e) {
            throw new DatabaseException("Error creating complaint: " + e.getMessage(), e);
        }
    }

    public Complaint findById(Long complaintId) throws DatabaseException, ComplaintNotFoundException {
        String sql = """
                SELECT c.*, u.name, s.registration_number
                FROM complaints c
                JOIN students s ON c.student_id = s.student_id
                JOIN users u ON s.student_id = u.id
                WHERE c.complaint_id = ?
                """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, complaintId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            throw new ComplaintNotFoundException(complaintId);
        } catch (SQLException e) {
            throw new DatabaseException("Error finding complaint: " + e.getMessage(), e);
        }
    }

    public List<Complaint> findByStudent(Long studentId) throws DatabaseException {
        String sql = """
                SELECT c.*, u.name, s.registration_number
                FROM complaints c
                JOIN students s ON c.student_id = s.student_id
                JOIN users u ON s.student_id = u.id
                WHERE c.student_id = ?
                ORDER BY c.created_at DESC
                """;
        List<Complaint> list = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching student complaints: " + e.getMessage(), e);
        }
    }

    public List<Complaint> findAll() throws DatabaseException {
        String sql = """
                SELECT c.*, u.name, s.registration_number
                FROM complaints c
                JOIN students s ON c.student_id = s.student_id
                JOIN users u ON s.student_id = u.id
                ORDER BY c.created_at DESC
                """;
        List<Complaint> list = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching all complaints: " + e.getMessage(), e);
        }
    }

    public void updateStatus(Long complaintId, ComplaintStatus newStatus, String adminRemarks)
            throws DatabaseException, ComplaintNotFoundException {

        String sql = """
                UPDATE complaints
                SET status = ?, admin_remarks = ?, resolved_at = ?
                WHERE complaint_id = ?
                """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus.name());
            ps.setString(2, adminRemarks);

            if (newStatus == ComplaintStatus.RESOLVED || newStatus == ComplaintStatus.REJECTED) {
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }
            ps.setLong(4, complaintId);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new ComplaintNotFoundException(complaintId);
            }
            LoggerUtil.info(ComplaintJDBCDAO.class, "Updated complaint #" + complaintId + " status to " + newStatus);
        } catch (SQLException e) {
            throw new DatabaseException("Error updating complaint status: " + e.getMessage(), e);
        }
    }

    private Complaint mapRow(ResultSet rs) throws SQLException {
        Complaint c = new Complaint(
                rs.getLong("complaint_id"),
                rs.getLong("student_id"),
                ComplaintCategory.fromString(rs.getString("category")),
                rs.getString("description")
        );
        c.setStudentName(rs.getString("name"));
        c.setRegistrationNumber(rs.getString("registration_number"));
        c.setStatus(ComplaintStatus.fromString(rs.getString("status")));
        c.setAdminRemarks(rs.getString("admin_remarks"));

        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) c.setCreatedAt(created.toLocalDateTime());

        Timestamp resolved = rs.getTimestamp("resolved_at");
        if (resolved != null) c.setResolvedAt(resolved.toLocalDateTime());

        return c;
    }
}
