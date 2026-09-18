package com.foodflow.model;

import com.foodflow.model.enums.UserRole;
import com.foodflow.model.interfaces.Manageable;
import com.foodflow.model.interfaces.Reportable;

import java.util.HashMap;
import java.util.Map;

/**
 * Concrete Student entity extending User and implementing Manageable and Reportable.
 * Demonstrates:
 * - Inheritance (extends User)
 * - Multiple interface implementation (Manageable, Reportable)
 * - 'super' constructor invocation
 * - Method overriding (showDashboard, getRoleTitle, toString)
 * - Polymorphism
 */
public class Student extends User implements Manageable, Reportable {

    private String registrationNumber;
    private String course;
    private int year;
    private String hostel;
    private String roomNumber;
    private boolean isEnrolled;

    public Student() {
        super();
        this.setRole(UserRole.STUDENT);
        this.isEnrolled = true;
    }

    public Student(Long id, String name, String email, String phone, String passwordHash, String passwordSalt,
                   String registrationNumber, String course, int year, String hostel, String roomNumber, boolean isEnrolled) {
        // Invoking superclass constructor using 'super'
        super(id, name, email, phone, passwordHash, passwordSalt, UserRole.STUDENT);
        this.registrationNumber = registrationNumber;
        this.course = course;
        this.year = year;
        this.hostel = hostel;
        this.roomNumber = roomNumber;
        this.isEnrolled = isEnrolled;
    }

    @Override
    public void showDashboard() {
        System.out.println("==================================================");
        System.out.println(" Welcome to Student Mess Portal, " + getName() + "!");
        System.out.println(" Reg No: " + registrationNumber + " | Hostel: " + hostel + " (" + roomNumber + ")");
        System.out.println("==================================================");
    }

    @Override
    public String getRoleTitle() {
        return "Student (" + registrationNumber + ")";
    }

    @Override
    public String getDisplayName() {
        return getName() + " [" + registrationNumber + "]";
    }

    @Override
    public String getDetailsSummary() {
        return String.format("%s, Year %d | %s Room %s | Enrolled: %s", course, year, hostel, roomNumber, isEnrolled ? "Yes" : "No");
    }

    @Override
    public String getReportIdentifier() {
        return "STUDENT_" + registrationNumber;
    }

    @Override
    public Map<String, Object> generateMetricsMap() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("studentId", getId());
        metrics.put("regNo", registrationNumber);
        metrics.put("name", getName());
        metrics.put("hostel", hostel);
        metrics.put("room", roomNumber);
        metrics.put("enrolled", isEnrolled);
        return metrics;
    }

    // Getters and Setters
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getHostel() {
        return hostel;
    }

    public void setHostel(String hostel) {
        this.hostel = hostel;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public boolean isEnrolled() {
        return isEnrolled;
    }

    public void setEnrolled(boolean enrolled) {
        isEnrolled = enrolled;
    }

    @Override
    public String toString() {
        return String.format("Student[ID=%d, Name='%s', RegNo='%s', Course='%s', Hostel='%s-%s']",
                getId(), getName(), registrationNumber, course, hostel, roomNumber);
    }
}
