package com.foodflow.exception;

/**
 * Exception wrapping low-level SQL and JPA persistence failures.
 */
public class DatabaseException extends FoodFlowException {

    public DatabaseException(String message) {
        super(message, "ERR_DATABASE");
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, "ERR_DATABASE", cause);
    }
}
