package com.foodflow.exception;

/**
 * Exception thrown when a complaint has empty details or invalid transitions.
 */
public class InvalidComplaintException extends FoodFlowException {

    public InvalidComplaintException(String message) {
        super(message, "ERR_INVALID_COMPLAINT");
    }
}
