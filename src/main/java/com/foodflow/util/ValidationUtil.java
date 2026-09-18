package com.foodflow.util;

import com.foodflow.exception.InvalidInputException;

import java.util.regex.Pattern;

/**
 * Validation utility enforcing business rules and preventing malformed data.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9]{10,15}$");

    private static final Pattern REG_NO_PATTERN =
            Pattern.compile("^[0-9]{2}[A-Z]{3}[0-9]{4}$");

    public static void validateNotEmpty(String value, String fieldName) throws InvalidInputException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(fieldName, fieldName + " cannot be empty or blank.");
        }
    }

    public static void validateEmail(String email) throws InvalidInputException {
        validateNotEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new InvalidInputException("Email", "Invalid email format (e.g., student@vit.ac.in).");
        }
    }

    public static void validatePhone(String phone) throws InvalidInputException {
        validateNotEmpty(phone, "Phone");
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new InvalidInputException("Phone", "Phone number must contain 10 to 15 digits.");
        }
    }

    public static void validateRegistrationNumber(String regNo) throws InvalidInputException {
        validateNotEmpty(regNo, "Registration Number");
        if (!REG_NO_PATTERN.matcher(regNo.trim().toUpperCase()).matches()) {
            throw new InvalidInputException("Registration Number", "Registration number format must match (e.g., 23BCE1001).");
        }
    }

    public static void validateRating(int rating) throws InvalidInputException {
        if (rating < 1 || rating > 5) {
            throw new InvalidInputException("Rating", "Star rating must be between 1 and 5.");
        }
    }

    public static void validatePasswordStrength(String password) throws InvalidInputException {
        validateNotEmpty(password, "Password");
        if (password.length() < 6) {
            throw new InvalidInputException("Password", "Password must be at least 6 characters long.");
        }
    }
}
