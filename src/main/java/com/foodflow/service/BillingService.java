package com.foodflow.service;

import com.foodflow.config.DatabaseManager;
import com.foodflow.dao.AttendanceJDBCDAO;
import com.foodflow.dao.StudentJDBCDAO;
import com.foodflow.exception.DatabaseException;
import com.foodflow.exception.FoodFlowException;
import com.foodflow.model.Bill;
import com.foodflow.model.MealAttendance;
import com.foodflow.model.Student;
import com.foodflow.model.enums.MealType;
import com.foodflow.model.enums.PaymentStatus;
import com.foodflow.util.LoggerUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service managing meal rate accounting, dynamic invoice calculation, and payment transactions.
 */
public class BillingService {

    private final DatabaseManager dbManager;
    private final AttendanceJDBCDAO attendanceDAO;
    private final StudentJDBCDAO studentDAO;

    public BillingService() {
        this.dbManager = DatabaseManager.getInstance();
        this.attendanceDAO = new AttendanceJDBCDAO();
        this.studentDAO = new StudentJDBCDAO();
    }

    /**
     * Calculates and generates a monthly bill for a student.
     */
    public Bill generateMonthlyBill(Long studentId, String monthName, int year, int month) throws FoodFlowException {
        Student student = studentDAO.findById(studentId);

        // Fetch all attendance for student
        List<MealAttendance> allAttendance = attendanceDAO.findByStudent(studentId);
        int totalMeals = 0;
        double calculatedAmount = 0.0;

        for (MealAttendance a : allAttendance) {
            if (a.getAttendanceDate().getYear() == year && a.getAttendanceDate().getMonthValue() == month) {
                totalMeals++;
                calculatedAmount += a.getMealType().getStandardRate();
            }
        }

        BigDecimal amount = BigDecimal.valueOf(calculatedAmount);

        // Upsert into bills table
        String checkSql = "SELECT bill_id, payment_status FROM bills WHERE student_id = ? AND billing_month = ?";
        String insertSql = "INSERT INTO bills (student_id, billing_month, total_meals, amount, payment_status) VALUES (?, ?, ?, ?, ?)";
        String updateSql = "UPDATE bills SET total_meals = ?, amount = ? WHERE student_id = ? AND billing_month = ? AND payment_status = 'PENDING'";

        try (Connection conn = dbManager.getConnection()) {
            Long existingBillId = null;
            PaymentStatus status = PaymentStatus.PENDING;

            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setLong(1, studentId);
                psCheck.setString(2, monthName);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        existingBillId = rs.getLong("bill_id");
                        status = PaymentStatus.fromString(rs.getString("payment_status"));
                    }
                }
            }

            Bill bill = new Bill(existingBillId, studentId, monthName, totalMeals, amount, status);
            bill.setStudentName(student.getName());
            bill.setRegistrationNumber(student.getRegistrationNumber());

            if (existingBillId == null) {
                try (PreparedStatement psInsert = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    psInsert.setLong(1, studentId);
                    psInsert.setString(2, monthName);
                    psInsert.setInt(3, totalMeals);
                    psInsert.setBigDecimal(4, amount);
                    psInsert.setString(5, status.name());
                    psInsert.executeUpdate();

                    try (ResultSet rs = psInsert.getGeneratedKeys()) {
                        if (rs.next()) {
                            bill.setBillId(rs.getLong(1));
                        }
                    }
                }
            } else {
                try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                    psUpdate.setInt(1, totalMeals);
                    psUpdate.setBigDecimal(2, amount);
                    psUpdate.setLong(3, studentId);
                    psUpdate.setString(4, monthName);
                    psUpdate.executeUpdate();
                }
            }

            LoggerUtil.info(BillingService.class,
                    String.format("Generated Bill: Student #%d (%s) | Month: %s | Meals: %d | Amount: ₹%.2f",
                            studentId, student.getRegistrationNumber(), monthName, totalMeals, amount.doubleValue()));
            return bill;
        } catch (SQLException e) {
            throw new DatabaseException("Error generating bill: " + e.getMessage(), e);
        }
    }

    public List<Bill> getBillsByStudent(Long studentId) throws DatabaseException {
        String sql = """
                SELECT b.*, u.name as student_name, s.registration_number
                FROM bills b
                JOIN students s ON b.student_id = s.student_id
                JOIN users u ON s.student_id = u.id
                WHERE b.student_id = ?
                ORDER BY b.generated_at DESC
                """;
        List<Bill> list = new ArrayList<>();
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
            throw new DatabaseException("Error fetching student bills: " + e.getMessage(), e);
        }
    }

    public List<Bill> getAllBills() throws DatabaseException {
        String sql = """
                SELECT b.*, u.name as student_name, s.registration_number
                FROM bills b
                JOIN students s ON b.student_id = s.student_id
                JOIN users u ON s.student_id = u.id
                ORDER BY b.generated_at DESC
                """;
        List<Bill> list = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching all bills: " + e.getMessage(), e);
        }
    }

    public void markBillPaid(Long billId) throws DatabaseException {
        String sql = "UPDATE bills SET payment_status = 'PAID', paid_at = ? WHERE bill_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setLong(2, billId);
            ps.executeUpdate();
            LoggerUtil.info(BillingService.class, "Bill #" + billId + " marked as PAID.");
        } catch (SQLException e) {
            throw new DatabaseException("Error marking bill paid: " + e.getMessage(), e);
        }
    }

    public BigDecimal calculateTotalRevenue() throws DatabaseException {
        String sql = "SELECT SUM(amount) FROM bills WHERE payment_status = 'PAID'";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal(1);
                return (total != null) ? total : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new DatabaseException("Error calculating total revenue: " + e.getMessage(), e);
        }
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        Bill b = new Bill(
                rs.getLong("bill_id"),
                rs.getLong("student_id"),
                rs.getString("billing_month"),
                rs.getInt("total_meals"),
                rs.getBigDecimal("amount"),
                PaymentStatus.fromString(rs.getString("payment_status"))
        );
        b.setStudentName(rs.getString("student_name"));
        b.setRegistrationNumber(rs.getString("registration_number"));
        Timestamp gen = rs.getTimestamp("generated_at");
        if (gen != null) b.setGeneratedAt(gen.toLocalDateTime());
        Timestamp paid = rs.getTimestamp("paid_at");
        if (paid != null) b.setPaidAt(paid.toLocalDateTime());
        return b;
    }
}
