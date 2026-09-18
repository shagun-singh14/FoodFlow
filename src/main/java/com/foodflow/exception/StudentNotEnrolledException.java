package com.foodflow.exception;

/**
 * Exception thrown when a student attempts mess operations while inactive or not enrolled.
 */
public class StudentNotEnrolledException extends FoodFlowException {

    public StudentNotEnrolledException(Long studentId) {
        super("Student with ID " + studentId + " is not currently enrolled in the mess plan.", "ERR_STUDENT_NOT_ENROLLED");
    }

    public StudentNotEnrolledException(String registrationNumber) {
        super("Student with Registration Number '" + registrationNumber + "' is not enrolled in the mess.", "ERR_STUDENT_NOT_ENROLLED");
    }
}
