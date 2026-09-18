package com.foodflow.exception;

/**
 * Exception thrown when input validation (e.g. email format, phone, empty strings) fails.
 */
public class InvalidInputException extends FoodFlowException {

    private final String fieldName;

    public InvalidInputException(String fieldName, String message) {
        super(String.format("Validation error on '%s': %s", fieldName, message), "ERR_INVALID_INPUT");
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
