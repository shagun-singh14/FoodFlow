package com.foodflow.model;

import com.foodflow.model.enums.UserRole;
import com.foodflow.model.interfaces.Manageable;

/**
 * Concrete Admin entity representing Mess Wardens, Caterers, and Administrative Officers.
 * Demonstrates:
 * - Inheritance (extends User)
 * - Interface implementation (Manageable)
 * - Polymorphism (showDashboard)
 * - 'super' usage
 */
public class Admin extends User implements Manageable {

    private String department;
    private String designation;

    public Admin() {
        super();
        this.setRole(UserRole.ADMIN);
    }

    public Admin(Long id, String name, String email, String phone, String passwordHash, String passwordSalt,
                 String department, String designation) {
        super(id, name, email, phone, passwordHash, passwordSalt, UserRole.ADMIN);
        this.department = department;
        this.designation = designation;
    }

    @Override
    public void showDashboard() {
        System.out.println("==================================================");
        System.out.println(" Welcome to FoodFlow Administrative Console, " + getName() + "!");
        System.out.println(" Designation: " + designation + " | Dept: " + department);
        System.out.println(" Full System Analytics & Management Enabled.");
        System.out.println("==================================================");
    }

    @Override
    public String getRoleTitle() {
        return "Mess Administrator (" + (designation != null ? designation : "Officer") + ")";
    }

    @Override
    public String getDisplayName() {
        return getName() + " [Admin]";
    }

    @Override
    public String getDetailsSummary() {
        return String.format("Admin Profile | Dept: %s | Designation: %s | Contact: %s", department, designation, getPhone());
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @Override
    public String toString() {
        return String.format("Admin[ID=%d, Name='%s', Dept='%s', Designation='%s']",
                getId(), getName(), department, designation);
    }
}
