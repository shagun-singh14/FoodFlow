package com.foodflow.dao;

import com.foodflow.config.DatabaseManager;
import com.foodflow.exception.DatabaseException;
import com.foodflow.exception.DuplicateMealAttendanceException;
import com.foodflow.model.MealAttendance;
import com.foodflow.model.enums.MealType;
import com.foodflow.util.LoggerUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Direct JDBC DAO for meal attendance transactions.
 * Demonstrates:
 * - PreparedStatement duplicate check
 * - Transaction isolation
 * - Attendance aggregation queries
 */
public class AttendanceJDBCDAO {

    private final DatabaseManager dbManager;

    public AttendanceJDBCDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Records a student meal attendance check.
     */
    public MealAttendance recordAttendance(Long studentId, LocalDate date, MealType mealType)
            throws DatabaseException, DuplicateMealAttendanceException {

        String checkSql = "SELECT attendance_id FROM meal_attendance WHERE student_id = ? AND attendance_date = ? AND meal_type = ?";
        String insertSql = "INSERT INTO meal_attendance (student_id, attendance_date, meal_type, status, marked_at) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = dbManager.getConnection();

            // 1. Check for duplicate record
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setLong(1, studentId);
                checkStmt.setDate(2, Date.valueOf(date));
                checkStmt.setString(3, mealType.name());

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        throw new DuplicateMealAttendanceException(studentId, date, mealType);
                    }
                }
            }

            // 2. Insert attendance record
            MealAttendance attendance = new MealAttendance(null, studentId, date, mealType, "PRESENT");
            attendance.setMarkedAt(LocalDateTime.now());

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setLong(1, studentId);
                insertStmt.setDate(2, Date.valueOf(date));
                insertStmt.setString(3, mealType.name());
                insertStmt.setString(4, attendance.getStatus());
                insertStmt.setTimestamp(5, Timestamp.valueOf(attendance.getMarkedAt()));
                insertStmt.executeUpdate();

                try (ResultSet rs = insertStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        attendance.setAttendanceId(rs.getLong(1));
                    }
                }
            }

            LoggerUtil.info(AttendanceJDBCDAO.class,
                    String.format("Attendance marked: Student #%d | Date: %s | Meal: %s", studentId, date, mealType.name()));
            return attendance;
        } catch (SQLException e) {
            // Check for MySQL duplicate key violation (SQLState 23000)
            if ("23000".equals(e.getSQLState())) {
                throw new DuplicateMealAttendanceException(studentId, date, mealType);
            }
            throw new DatabaseException("Error recording meal attendance: " + e.getMessage(), e);
        } finally {
            DatabaseManager.close(conn);
        }
    }

    public boolean hasAttended(Long studentId, LocalDate date, MealType mealType) throws DatabaseException {
        String sql = "SELECT 1 FROM meal_attendance WHERE student_id = ? AND attendance_date = ? AND meal_type = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, studentId);
            ps.setDate(2, Date.valueOf(date));
            ps.setString(3, mealType.name());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking attendance: " + e.getMessage(), e);
        }
    }

    public List<MealAttendance> findByStudent(Long studentId) throws DatabaseException {
        String sql = """
                SELECT a.*, u.name, s.registration_number
                FROM meal_attendance a
                JOIN students s ON a.student_id = s.student_id
                JOIN users u ON s.student_id = u.id
                WHERE a.student_id = ?
                ORDER BY a.attendance_date DESC, a.marked_at DESC
                """;
        List<MealAttendance> list = new ArrayList<>();
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
            throw new DatabaseException("Error fetching student attendance: " + e.getMessage(), e);
        }
    }

    public List<MealAttendance> findByDate(LocalDate date) throws DatabaseException {
        String sql = """
                SELECT a.*, u.name, s.registration_number
                FROM meal_attendance a
                JOIN students s ON a.student_id = s.student_id
                JOIN users u ON s.student_id = u.id
                WHERE a.attendance_date = ?
                ORDER BY a.marked_at DESC
                """;
        List<MealAttendance> list = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching attendance by date: " + e.getMessage(), e);
        }
    }

    public int countMealsByStudentAndMonth(Long studentId, int year, int month) throws DatabaseException {
        String sql = """
                SELECT COUNT(*) FROM meal_attendance
                WHERE student_id = ?
                  AND YEAR(attendance_date) = ?
                  AND MONTH(attendance_date) = ?
                """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, studentId);
            ps.setInt(2, year);
            ps.setInt(3, month);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error counting monthly meals: " + e.getMessage(), e);
        }
    }

    private MealAttendance mapRow(ResultSet rs) throws SQLException {
        MealAttendance a = new MealAttendance(
                rs.getLong("attendance_id"),
                rs.getLong("student_id"),
                rs.getDate("attendance_date").toLocalDate(),
                MealType.fromString(rs.getString("meal_type")),
                rs.getString("status")
        );
        a.setStudentName(rs.getString("name"));
        a.setRegistrationNumber(rs.getString("registration_number"));
        Timestamp ts = rs.getTimestamp("marked_at");
        if (ts != null) {
            a.setMarkedAt(ts.toLocalDateTime());
        }
        return a;
    }
}
