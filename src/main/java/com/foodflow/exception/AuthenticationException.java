package com.foodflow.exception;

/**
 * Exception thrown for invalid credentials, unauthorized role access, or session expirations.
 */
public class AuthenticationException extends FoodFlowException {

    public AuthenticationException(String message) {
        super(message, "ERR_AUTH_FAILED");
    }
}
