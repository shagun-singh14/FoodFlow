package com.foodflow;

import com.foodflow.exception.AuthenticationException;
import com.foodflow.exception.FoodFlowException;
import com.foodflow.model.Student;
import com.foodflow.model.User;
import com.foodflow.model.enums.UserRole;
import com.foodflow.service.AuthService;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthServiceTest {

    private static AuthService authService;

    @BeforeAll
    public static void setup() {
        authService = new AuthService();
    }

    @Test
    @Order(1)
    public void testStudentLoginSuccess() throws FoodFlowException {
        User user = authService.login("rahul.sharma@vit.ac.in", "Password@123");
        assertNotNull(user);
        assertEquals("Rahul Sharma", user.getName());
        assertEquals(UserRole.STUDENT, user.getRole());
    }

    @Test
    @Order(2)
    public void testAdminLoginSuccess() throws FoodFlowException {
        User user = authService.login("admin@foodflow.edu", "Password@123");
        assertNotNull(user);
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    @Order(3)
    public void testLoginInvalidPasswordThrowsAuthenticationException() {
        assertThrows(AuthenticationException.class, () -> {
            authService.login("rahul.sharma@vit.ac.in", "WrongPassword999");
        });
    }

    @Test
    @Order(4)
    public void testStudentRegistrationSuccess() throws FoodFlowException {
        String testEmail = "test.student." + System.currentTimeMillis() + "@vit.ac.in";
        String testRegNo = "24BCE" + (int)(Math.random() * 8999 + 1000);

        Student s = authService.registerStudent(
                "Test New Student",
                testEmail,
                "9888777666",
                "Password@123",
                testRegNo,
                "B.Tech CSE",
                1,
                "Block A - Mens Hostel",
                "A-101"
        );

        assertNotNull(s);
        assertNotNull(s.getId());
        assertEquals(testEmail, s.getEmail());
    }
}
