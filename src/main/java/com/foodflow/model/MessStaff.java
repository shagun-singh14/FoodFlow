package com.foodflow.model;

import com.foodflow.model.interfaces.Manageable;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model representing operational mess staff (Chefs, Hygiene Supervisors, Dining leads).
 */
public class MessStaff implements Manageable, Serializable {

    private static final long serialVersionUID = 1L;

    private Long staffId;
    private String name;
    private String role;
    private String phone;
    private String shift;
    private LocalDateTime createdAt;

    public MessStaff() {
        this.createdAt = LocalDateTime.now();
    }

    public MessStaff(Long staffId, String name, String role, String phone, String shift) {
        this();
        this.staffId = staffId;
        this.name = name;
        this.role = role;
        this.phone = phone;
        this.shift = shift;
    }

    @Override
    public Long getId() {
        return staffId;
    }

    @Override
    public String getDisplayName() {
        return name + " (" + role + ")";
    }

    @Override
    public String getDetailsSummary() {
        return String.format("Role: %s | Shift: %s | Contact: %s", role, shift, phone);
    }

    // Getters and Setters
    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("Staff[ID=%d, Name='%s', Role='%s', Shift='%s']", staffId, name, role, shift);
    }
}
