package com.foodflow.exception;

/**
 * Exception thrown when a requested student ID or registration number is not found.
 */
public class StudentNotFoundException extends FoodFlowException {

    public StudentNotFoundException(Long studentId) {
        super("Student with ID #" + studentId + " was not found in the records.", "ERR_STUDENT_NOT_FOUND");
    }

    public StudentNotFoundException(String regNo) {
        super("Student with Registration Number '" + regNo + "' was not found.", "ERR_STUDENT_NOT_FOUND");
    }
}
