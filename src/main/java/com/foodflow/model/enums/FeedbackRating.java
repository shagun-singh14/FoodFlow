package com.foodflow.model.enums;

/**
 * Enumeration representing star ratings (1 to 5) for meal quality feedback.
 */
public enum FeedbackRating {
    ONE(1, "Poor", "★☆☆☆☆"),
    TWO(2, "Fair", "★★☆☆☆"),
    THREE(3, "Good", "★★★☆☆"),
    FOUR(4, "Very Good", "★★★★☆"),
    FIVE(5, "Excellent", "★★★★★");

    private final int value;
    private final String description;
    private final String stars;

    FeedbackRating(int value, String description, String stars) {
        this.value = value;
        this.description = description;
        this.stars = stars;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public String getStars() {
        return stars;
    }

    public static FeedbackRating fromInt(int ratingVal) {
        for (FeedbackRating r : values()) {
            if (r.getValue() == ratingVal) {
                return r;
            }
        }
        return THREE;
    }
}
