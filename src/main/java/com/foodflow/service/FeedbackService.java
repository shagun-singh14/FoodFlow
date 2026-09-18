package com.foodflow.service;

import com.foodflow.config.DatabaseManager;
import com.foodflow.exception.DatabaseException;
import com.foodflow.exception.FoodFlowException;
import com.foodflow.model.Feedback;
import com.foodflow.model.enums.MealType;
import com.foodflow.util.LoggerUtil;
import com.foodflow.util.ValidationUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service managing student meal ratings and quality feedback analytics.
 * Demonstrates:
 * - Collections: ArrayList for feedback lists, Map for aggregate statistics
 * - SQL aggregation (AVG, COUNT)
 */
public class FeedbackService {

    private final DatabaseManager dbManager;

    public FeedbackService() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public Feedback submitFeedback(Long studentId, MealType mealType, int rating, String comments) throws FoodFlowException {
        ValidationUtil.validateRating(rating);

        String sql = "INSERT INTO feedbacks (student_id, meal_type, rating, comments, feedback_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Feedback feedback = new Feedback(null, studentId, mealType, rating, comments, LocalDate.now());

            ps.setLong(1, studentId);
            ps.setString(2, mealType.name());
            ps.setInt(3, rating);
            ps.setString(4, comments);
            ps.setDate(5, Date.valueOf(feedback.getFeedbackDate()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    feedback.setFeedbackId(rs.getLong(1));
                }
            }
            LoggerUtil.info(FeedbackService.class,
                    String.format("Feedback submitted by Student #%d for %s (Rating: %d/5)", studentId, mealType.getLabel(), rating));
            return feedback;
        } catch (SQLException e) {
            throw new DatabaseException("Error submitting feedback: " + e.getMessage(), e);
        }
    }

    public List<Feedback> getAllFeedbacks() throws DatabaseException {
        String sql = """
                SELECT f.*, u.name as student_name
                FROM feedbacks f
                JOIN students s ON f.student_id = s.student_id
                JOIN users u ON s.student_id = u.id
                ORDER BY f.created_at DESC
                """;
        List<Feedback> list = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Feedback f = new Feedback(
                        rs.getLong("feedback_id"),
                        rs.getLong("student_id"),
                        MealType.fromString(rs.getString("meal_type")),
                        rs.getInt("rating"),
                        rs.getString("comments"),
                        rs.getDate("feedback_date").toLocalDate()
                );
                f.setStudentName(rs.getString("student_name"));
                list.add(f);
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching feedbacks: " + e.getMessage(), e);
        }
    }

    public List<Feedback> getFeedbacksByStudent(Long studentId) throws DatabaseException {
        String sql = "SELECT * FROM feedbacks WHERE student_id = ? ORDER BY feedback_date DESC";
        List<Feedback> list = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Feedback(
                            rs.getLong("feedback_id"),
                            rs.getLong("student_id"),
                            MealType.fromString(rs.getString("meal_type")),
                            rs.getInt("rating"),
                            rs.getString("comments"),
                            rs.getDate("feedback_date").toLocalDate()
                    ));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching student feedbacks: " + e.getMessage(), e);
        }
    }

    public double calculateOverallAverageRating() throws DatabaseException {
        String sql = "SELECT AVG(rating) FROM feedbacks";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                double avg = rs.getDouble(1);
                return Math.round(avg * 100.0) / 100.0;
            }
            return 0.0;
        } catch (SQLException e) {
            throw new DatabaseException("Error calculating average rating: " + e.getMessage(), e);
        }
    }

    public Map<MealType, Double> getAverageRatingByMealType() throws DatabaseException {
        String sql = "SELECT meal_type, AVG(rating) as avg_rating FROM feedbacks GROUP BY meal_type";
        Map<MealType, Double> map = new HashMap<>();
        for (MealType m : MealType.values()) {
            map.put(m, 0.0);
        }

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                MealType type = MealType.fromString(rs.getString("meal_type"));
                double avg = Math.round(rs.getDouble("avg_rating") * 10.0) / 10.0;
                map.put(type, avg);
            }
            return map;
        } catch (SQLException e) {
            throw new DatabaseException("Error calculating meal-wise ratings: " + e.getMessage(), e);
        }
    }
}
