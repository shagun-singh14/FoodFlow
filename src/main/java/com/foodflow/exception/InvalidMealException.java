package com.foodflow.exception;

/**
 * Exception thrown when a meal type, timing, or menu entry is invalid.
 */
public class InvalidMealException extends FoodFlowException {

    public InvalidMealException(String message) {
        super(message, "ERR_INVALID_MEAL");
    }
}
