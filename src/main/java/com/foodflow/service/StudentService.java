package com.foodflow.service;

import com.foodflow.dao.StudentJDBCDAO;
import com.foodflow.exception.DatabaseException;
import com.foodflow.exception.FoodFlowException;
import com.foodflow.exception.StudentNotFoundException;
import com.foodflow.model.Student;
import com.foodflow.util.LoggerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing student profiles, mess enrollment, and search filters.
 * Demonstrates:
 * - Method Overloading (searchStudent by Name, ID, Name & Course)
 * - Collections: ArrayList usage for student listings
 * - Encapsulation & Business logic delegation
 */
public class StudentService {

    private final StudentJDBCDAO studentDAO;

    public StudentService() {
        this.studentDAO = new StudentJDBCDAO();
    }

    public List<Student> getAllStudents() throws DatabaseException {
        return studentDAO.findAll();
    }

    public Student getStudentById(Long id) throws FoodFlowException {
        return studentDAO.findById(id);
    }

    public Student getStudentByRegNo(String regNo) throws DatabaseException {
        return studentDAO.findByRegistrationNumber(regNo);
    }

    public void updateStudent(Student student) throws DatabaseException {
        studentDAO.updateStudent(student);
    }

    public void deactivateStudent(Long studentId) throws FoodFlowException {
        Student s = studentDAO.findById(studentId);
        s.setEnrolled(false);
        studentDAO.updateStudent(s);
        LoggerUtil.info(StudentService.class, "Deactivated mess enrollment for student ID: " + studentId);
    }

    public void reactivateStudent(Long studentId) throws FoodFlowException {
        Student s = studentDAO.findById(studentId);
        s.setEnrolled(true);
        studentDAO.updateStudent(s);
        LoggerUtil.info(StudentService.class, "Reactivated mess enrollment for student ID: " + studentId);
    }

    // =========================================================================
    // METHOD OVERLOADING DEMONSTRATION
    // =========================================================================

    /**
     * Overloaded Search 1: Search students matching name (case-insensitive substring).
     */
    public List<Student> searchStudent(String name) throws DatabaseException {
        List<Student> all = studentDAO.findAll();
        if (name == null || name.trim().isEmpty()) return all;

        String query = name.trim().toLowerCase();
        return all.stream()
                .filter(s -> s.getName().toLowerCase().contains(query) || s.getRegistrationNumber().toLowerCase().contains(query))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Overloaded Search 2: Search single student by numeric ID.
     */
    public List<Student> searchStudent(int id) throws DatabaseException {
        List<Student> result = new ArrayList<>();
        try {
            Student s = studentDAO.findById((long) id);
            result.add(s);
        } catch (StudentNotFoundException e) {
            // Return empty list if not found
        }
        return result;
    }

    /**
     * Overloaded Search 3: Search students filtering by both name and course.
     */
    public List<Student> searchStudent(String name, String course) throws DatabaseException {
        List<Student> all = studentDAO.findAll();
        String nameQuery = (name != null) ? name.trim().toLowerCase() : "";
        String courseQuery = (course != null) ? course.trim().toLowerCase() : "";

        return all.stream()
                .filter(s -> (nameQuery.isEmpty() || s.getName().toLowerCase().contains(nameQuery))
                          && (courseQuery.isEmpty() || s.getCourse().toLowerCase().contains(courseQuery)))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
