package com.foodflow;

import com.foodflow.model.Student;
import com.foodflow.service.StudentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StudentServiceTest {

    private static StudentService studentService;

    @BeforeAll
    public static void setup() {
        studentService = new StudentService();
    }

    @Test
    public void testGetAllStudents() throws Exception {
        List<Student> students = studentService.getAllStudents();
        assertNotNull(students);
        assertTrue(students.size() >= 10, "Should have at least 10 seed students");
    }

    @Test
    public void testMethodOverloadingSearchByName() throws Exception {
        List<Student> result = studentService.searchStudent("Rahul");
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).getName().contains("Rahul"));
    }

    @Test
    public void testMethodOverloadingSearchById() throws Exception {
        List<Student> result = studentService.searchStudent(3);
        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getId());
    }

    @Test
    public void testMethodOverloadingSearchByCombo() throws Exception {
        List<Student> result = studentService.searchStudent("Ananya", "B.Tech CSE");
        assertFalse(result.isEmpty());
        assertEquals("Ananya Patel", result.get(0).getName());
    }
}
