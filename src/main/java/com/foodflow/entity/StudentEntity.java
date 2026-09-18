package com.foodflow.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity mapping the 'students' table with One-to-Many relationships.
 * Demonstrates @PrimaryKeyJoinColumn, @OneToMany, cascade configurations.
 */
@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "student_id")
public class StudentEntity extends UserEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "registration_number", nullable = false, unique = true, length = 30)
    private String registrationNumber;

    @Column(name = "course", nullable = false, length = 50)
    private String course;

    @Column(name = "academic_year", nullable = false)
    private int year;

    @Column(name = "hostel", nullable = false, length = 50)
    private String hostel;

    @Column(name = "room_number", nullable = false, length = 20)
    private String roomNumber;

    @Column(name = "is_enrolled", nullable = false)
    private boolean isEnrolled = true;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AttendanceEntity> attendances = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ComplaintEntity> complaints = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FeedbackEntity> feedbacks = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BillEntity> bills = new ArrayList<>();

    public StudentEntity() {
        super();
        setRole("STUDENT");
    }

    public StudentEntity(String name, String email, String phone, String passwordHash, String passwordSalt,
                         String registrationNumber, String course, int year, String hostel, String roomNumber, boolean isEnrolled) {
        super(name, email, phone, passwordHash, passwordSalt, "STUDENT");
        this.registrationNumber = registrationNumber;
        this.course = course;
        this.year = year;
        this.hostel = hostel;
        this.roomNumber = roomNumber;
        this.isEnrolled = isEnrolled;
    }

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

    public List<AttendanceEntity> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<AttendanceEntity> attendances) {
        this.attendances = attendances;
    }

    public List<ComplaintEntity> getComplaints() {
        return complaints;
    }

    public void setComplaints(List<ComplaintEntity> complaints) {
        this.complaints = complaints;
    }

    public List<FeedbackEntity> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<FeedbackEntity> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public List<BillEntity> getBills() {
        return bills;
    }

    public void setBills(List<BillEntity> bills) {
        this.bills = bills;
    }
}
