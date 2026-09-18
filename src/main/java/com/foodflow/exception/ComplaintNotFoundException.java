package com.foodflow.exception;

/**
 * Exception thrown when an admin or student accesses a complaint ID that does not exist.
 */
public class ComplaintNotFoundException extends FoodFlowException {

    public ComplaintNotFoundException(Long complaintId) {
        super("Complaint with ID #" + complaintId + " was not found in the records.", "ERR_COMPLAINT_NOT_FOUND");
    }
}
