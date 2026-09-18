package com.foodflow.dao;

import com.foodflow.config.DatabaseManager;
import com.foodflow.exception.DatabaseException;
import com.foodflow.exception.StudentNotFoundException;
import com.foodflow.model.Student;
import com.foodflow.model.enums.UserRole;
import com.foodflow.util.LoggerUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Direct JDBC DAO for Student persistence operations.
 * Demonstrates:
 * - Connection management
 * - PreparedStatements with parameter binding
 * - ResultSet iteration and Object-Relational mapping
 * - Transaction control (commit & rollback)
 * - Custom exception handling
 */
public class StudentJDBCDAO {

    private final DatabaseManager dbManager;

    public StudentJDBCDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Inserts a new student using a two-table transactional PreparedStatement.
     */
    public Student createStudent(Student student) throws DatabaseException {
        String insertUserSql = "INSERT INTO users (name, email, phone, password_hash, password_salt, role) VALUES (?, ?, ?, ?, ?, ?)";
        String insertStudentSql = "INSERT INTO students (student_id, registration_number, course, academic_year, hostel, room_number, is_enrolled) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement userStmt = null;
        PreparedStatement studentStmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = dbManager.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Insert into users table
            userStmt = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, student.getName());
            userStmt.setString(2, student.getEmail());
            userStmt.setString(3, student.getPhone());
            userStmt.setString(4, student.getPasswordHash());
            userStmt.setString(5, student.getPasswordSalt());
            userStmt.setString(6, student.getRole().name());
            userStmt.executeUpdate();

            generatedKeys = userStmt.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new DatabaseException("Failed to retrieve generated user ID.");
            }
            long userId = generatedKeys.getLong(1);
            student.setId(userId);

            // 2. Insert into students table
            studentStmt = conn.prepareStatement(insertStudentSql);
            studentStmt.setLong(1, userId);
            studentStmt.setString(2, student.getRegistrationNumber());
            studentStmt.setString(3, student.getCourse());
            studentStmt.setInt(4, student.getYear());
            studentStmt.setString(5, student.getHostel());
            studentStmt.setString(6, student.getRoomNumber());
            studentStmt.setBoolean(7, student.isEnrolled());
            studentStmt.executeUpdate();

            conn.commit(); // Commit transaction
            LoggerUtil.info(StudentJDBCDAO.class, "Created Student: " + student.getRegistrationNumber() + " (ID: " + userId + ")");
            return student;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignored */ }
            }
            throw new DatabaseException("Error creating student: " + e.getMessage(), e);
        } finally {
            DatabaseManager.close(generatedKeys, userStmt, studentStmt, conn);
        }
    }

    /**
     * Finds a student by ID.
     */
    public Student findById(Long studentId) throws DatabaseException, StudentNotFoundException {
        String sql = """
                SELECT u.id, u.name, u.email, u.phone, u.password_hash, u.password_salt, u.role,
                       s.registration_number, s.course, s.academic_year, s.hostel, s.room_number, s.is_enrolled
                FROM users u
                JOIN students s ON u.id = s.student_id
                WHERE u.id = ?
                """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudent(rs);
                }
            }
            throw new StudentNotFoundException(studentId);
        } catch (SQLException e) {
            throw new DatabaseException("Error finding student by ID: " + e.getMessage(), e);
        }
    }

    /**
     * Finds a student by registration number.
     */
    public Student findByRegistrationNumber(String regNo) throws DatabaseException {
        String sql = """
                SELECT u.id, u.name, u.email, u.phone, u.password_hash, u.password_salt, u.role,
                       s.registration_number, s.course, s.academic_year, s.hostel, s.room_number, s.is_enrolled
                FROM users u
                JOIN students s ON u.id = s.student_id
                WHERE UPPER(s.registration_number) = UPPER(?)
                """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, regNo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudent(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding student by registration number: " + e.getMessage(), e);
        }
    }

    /**
     * Finds a student by email.
     */
    public Student findByEmail(String email) throws DatabaseException {
        String sql = """
                SELECT u.id, u.name, u.email, u.phone, u.password_hash, u.password_salt, u.role,
                       s.registration_number, s.course, s.academic_year, s.hostel, s.room_number, s.is_enrolled
                FROM users u
                JOIN students s ON u.id = s.student_id
                WHERE LOWER(u.email) = LOWER(?)
                """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudent(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding student by email: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all students.
     */
    public List<Student> findAll() throws DatabaseException {
        String sql = """
                SELECT u.id, u.name, u.email, u.phone, u.password_hash, u.password_salt, u.role,
                       s.registration_number, s.course, s.academic_year, s.hostel, s.room_number, s.is_enrolled
                FROM users u
                JOIN students s ON u.id = s.student_id
                ORDER BY s.registration_number ASC
                """;

        List<Student> list = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToStudent(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error listing students: " + e.getMessage(), e);
        }
    }

    /**
     * Updates student and user details.
     */
    public void updateStudent(Student student) throws DatabaseException {
        String updateUserSql = "UPDATE users SET name = ?, email = ?, phone = ? WHERE id = ?";
        String updateStudentSql = "UPDATE students SET course = ?, academic_year = ?, hostel = ?, room_number = ?, is_enrolled = ? WHERE student_id = ?";

        Connection conn = null;
        try {
            conn = dbManager.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psUser = conn.prepareStatement(updateUserSql);
                 PreparedStatement psStudent = conn.prepareStatement(updateStudentSql)) {

                psUser.setString(1, student.getName());
                psUser.setString(2, student.getEmail());
                psUser.setString(3, student.getPhone());
                psUser.setLong(4, student.getId());
                psUser.executeUpdate();

                psStudent.setString(1, student.getCourse());
                psStudent.setInt(2, student.getYear());
                psStudent.setString(3, student.getHostel());
                psStudent.setString(4, student.getRoomNumber());
                psStudent.setBoolean(5, student.isEnrolled());
                psStudent.setLong(6, student.getId());
                psStudent.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new DatabaseException("Error updating student: " + e.getMessage(), e);
        } finally {
            DatabaseManager.close(conn);
        }
    }

    /**
     * Deactivates or removes a student.
     */
    public void deleteStudent(Long studentId) throws DatabaseException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting student: " + e.getMessage(), e);
        }
    }

    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        Student s = new Student(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("password_hash"),
                rs.getString("password_salt"),
                rs.getString("registration_number"),
                rs.getString("course"),
                rs.getInt("academic_year"),
                rs.getString("hostel"),
                rs.getString("room_number"),
                rs.getBoolean("is_enrolled")
        );
        s.setRole(UserRole.fromString(rs.getString("role")));
        return s;
    }
}
