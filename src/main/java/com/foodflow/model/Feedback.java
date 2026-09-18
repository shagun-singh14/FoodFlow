package com.foodflow.model;

import com.foodflow.model.enums.FeedbackRating;
import com.foodflow.model.enums.MealType;
import com.foodflow.model.interfaces.Exportable;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model representing student reviews and star ratings on meals.
 */
public class Feedback implements Exportable, Serializable {

    private static final long serialVersionUID = 1L;

    private Long feedbackId;
    private Long studentId;
    private String studentName;
    private MealType mealType;
    private int rating;
    private String comments;
    private LocalDate feedbackDate;
    private LocalDateTime createdAt;

    public Feedback() {
        this.rating = 3;
        this.feedbackDate = LocalDate.now();
        this.createdAt = LocalDateTime.now();
    }

    public Feedback(Long feedbackId, Long studentId, MealType mealType, int rating, String comments, LocalDate feedbackDate) {
        this();
        this.feedbackId = feedbackId;
        this.studentId = studentId;
        this.mealType = mealType;
        this.rating = Math.max(1, Math.min(5, rating));
        this.comments = comments;
        this.feedbackDate = feedbackDate != null ? feedbackDate : LocalDate.now();
    }

    public FeedbackRating getFeedbackRatingEnum() {
        return FeedbackRating.fromInt(rating);
    }

    @Override
    public String toFormattedText() {
        return String.format("[%s] %s Feedback (Rating: %d/5 %s) - %s: \"%s\"",
                feedbackDate, mealType.getLabel(), rating, getFeedbackRatingEnum().getStars(),
                studentName != null ? studentName : "Student #" + studentId,
                comments != null ? comments : "");
    }

    @Override
    public String toCsvRow() {
        return String.format("%d,%d,\"%s\",\"%s\",%d,\"%s\",\"%s\"",
                feedbackId != null ? feedbackId : 0L,
                studentId,
                studentName != null ? studentName : "",
                mealType.name(),
                rating,
                comments != null ? comments.replace("\"", "\"\"") : "",
                feedbackDate);
    }

    @Override
    public String getCsvHeader() {
        return "FeedbackId,StudentId,StudentName,MealType,Rating,Comments,Date";
    }

    // Getters and Setters
    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
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

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = Math.max(1, Math.min(5, rating));
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDate getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(LocalDate feedbackDate) {
        this.feedbackDate = feedbackDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("Feedback[ID=%d, Student=%d, Meal=%s, Rating=%d, Date=%s]",
                feedbackId, studentId, mealType, rating, feedbackDate);
    }
}
