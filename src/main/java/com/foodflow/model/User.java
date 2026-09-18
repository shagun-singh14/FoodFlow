package com.foodflow.model;

import com.foodflow.model.enums.UserRole;
import com.foodflow.model.interfaces.Authenticatable;
import com.foodflow.util.PasswordUtil;

import java.time.LocalDateTime;

/**
 * Abstract base class representing a generic system User in FoodFlow.
 * Demonstrates:
 * - Abstract class with abstract and concrete methods
 * - Encapsulation (private fields with getters/setters)
 * - Constructor overloading & use of 'this'
 * - 'final' methods to prevent overriding security-critical logic
 * - Implementation of Authenticatable interface
 */
public abstract class User implements Authenticatable {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String passwordHash;
    private String passwordSalt;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default Constructor
    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Parameterized Constructor demonstrating 'this' keyword
    public User(Long id, String name, String email, String phone, String passwordHash, String passwordSalt, UserRole role) {
        this();
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.role = role;
    }

    /**
     * Abstract method to be overridden by subclasses (Student and Admin) to present role-specific dashboards.
     * Demonstrates Polymorphism.
     */
    public abstract void showDashboard();

    /**
     * Abstract method returning the role-specific view descriptor.
     */
    public abstract String getRoleTitle();

    /**
     * Final method for security authentication to ensure subclasses cannot bypass password validation.
     * Demonstrates 'final' method keyword.
     */
    @Override
    public final boolean authenticate(String plainPassword) {
        if (plainPassword == null || this.passwordHash == null) {
            return false;
        }
        return PasswordUtil.verifyPassword(plainPassword, this.passwordSalt, this.passwordHash);
    }

    // Getters and Setters demonstrating Encapsulation
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return String.format("%s [ID=%d, Name=%s, Email=%s, Role=%s]", getClass().getSimpleName(), id, name, email, role);
    }
}
