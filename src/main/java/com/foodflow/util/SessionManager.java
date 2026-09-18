package com.foodflow.util;

import com.foodflow.model.Admin;
import com.foodflow.model.Student;
import com.foodflow.model.User;
import com.foodflow.model.enums.UserRole;

/**
 * Singleton class managing current active user authentication session.
 * Demonstrates:
 * - Singleton Design Pattern (Thread-safe initialization)
 * - Session Tracking
 */
public class SessionManager {

    private static volatile SessionManager instance;

    private User currentUser;

    private SessionManager() {
        // Private constructor prevents instantiation
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
        LoggerUtil.info(SessionManager.class, "Session started for user: " + user.getEmail() + " (" + user.getRole() + ")");
    }

    public void logout() {
        if (currentUser != null) {
            LoggerUtil.info(SessionManager.class, "Session terminated for user: " + currentUser.getEmail());
        }
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == UserRole.ADMIN;
    }

    public boolean isStudent() {
        return currentUser != null && currentUser.getRole() == UserRole.STUDENT;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Student getCurrentStudent() {
        if (currentUser instanceof Student s) {
            return s;
        }
        return null;
    }

    public Admin getCurrentAdmin() {
        if (currentUser instanceof Admin a) {
            return a;
        }
        return null;
    }
}
