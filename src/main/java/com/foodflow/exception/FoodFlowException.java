package com.foodflow.exception;

/**
 * Base custom checked exception for all domain and operational failures in FoodFlow.
 */
public class FoodFlowException extends Exception {
    
    private final String errorCode;

    public FoodFlowException(String message) {
        super(message);
        this.errorCode = "ERR_GENERAL";
    }

    public FoodFlowException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public FoodFlowException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "ERR_WRAPPED";
    }

    public FoodFlowException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
