package com.foodflow.service;

import com.foodflow.config.DatabaseManager;
import com.foodflow.dao.StudentJDBCDAO;
import com.foodflow.exception.AuthenticationException;
import com.foodflow.exception.DatabaseException;
import com.foodflow.exception.FoodFlowException;
import com.foodflow.model.Admin;
import com.foodflow.model.Student;
import com.foodflow.model.User;
import com.foodflow.model.enums.UserRole;
import com.foodflow.util.LoggerUtil;
import com.foodflow.util.PasswordUtil;
import com.foodflow.util.SessionManager;
import com.foodflow.util.ValidationUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Service managing user authentication, registration, and credential security.
 */
public class AuthService {

    private final StudentJDBCDAO studentDAO;
    private final DatabaseManager dbManager;

    public AuthService() {
        this.studentDAO = new StudentJDBCDAO();
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Authenticates a user by email and plain password.
     */
    public User login(String email, String plainPassword) throws FoodFlowException {
        ValidationUtil.validateEmail(email);
        ValidationUtil.validateNotEmpty(plainPassword, "Password");

        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    LoggerUtil.warning(AuthService.class, "Failed login attempt: Email not found (" + email + ")");
                    throw new AuthenticationException("No account registered with email '" + email + "'.");
                }

                long userId = rs.getLong("id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String passwordHash = rs.getString("password_hash");
                String passwordSalt = rs.getString("password_salt");
                UserRole role = UserRole.fromString(rs.getString("role"));

                // Verify salted hash
                if (!PasswordUtil.verifyPassword(plainPassword, passwordSalt, passwordHash)) {
                    LoggerUtil.warning(AuthService.class, "Failed login attempt: Invalid password for (" + email + ")");
                    throw new AuthenticationException("Invalid password. Please check your credentials.");
                }

                User user;
                if (role == UserRole.STUDENT) {
                    user = studentDAO.findById(userId);
                } else {
                    user = new Admin(userId, name, email, phone, passwordHash, passwordSalt, "Mess Administration", "Chief Warden");
                }

                SessionManager.getInstance().login(user);
                LoggerUtil.info(AuthService.class, "Successful login: " + email + " as " + role);
                return user;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Authentication database error: " + e.getMessage(), e);
        }
    }

    /**
     * Registers a new student account.
     */
    public Student registerStudent(String name, String email, String phone, String password,
                                   String regNo, String course, int year, String hostel, String roomNumber)
            throws FoodFlowException {

        // Validations
        ValidationUtil.validateNotEmpty(name, "Name");
        ValidationUtil.validateEmail(email);
        ValidationUtil.validatePhone(phone);
        ValidationUtil.validatePasswordStrength(password);
        ValidationUtil.validateRegistrationNumber(regNo);
        ValidationUtil.validateNotEmpty(course, "Course");
        ValidationUtil.validateNotEmpty(hostel, "Hostel");
        ValidationUtil.validateNotEmpty(roomNumber, "Room Number");

        // Check if student or email already exists
        if (studentDAO.findByEmail(email) != null) {
            throw new FoodFlowException("An account with email '" + email + "' is already registered.", "ERR_DUPLICATE_EMAIL");
        }
        if (studentDAO.findByRegistrationNumber(regNo) != null) {
            throw new FoodFlowException("Registration number '" + regNo + "' is already registered in the system.", "ERR_DUPLICATE_REGNO");
        }

        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password, salt);

        Student student = new Student(
                null, name, email.trim(), phone.trim(), hash, salt,
                regNo.trim().toUpperCase(), course.trim(), year, hostel.trim(), roomNumber.trim(), true
        );

        return studentDAO.createStudent(student);
    }
}
