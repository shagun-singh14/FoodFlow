# Testing & Quality Assurance Documentation

## 1. Test Strategy Overview
FoodFlow employs automated unit and integration tests using JUnit 5 Jupiter, verifying critical business logic, security constraints, multi-threaded concurrency safety, and file I/O streams.

---

## 2. Test Suites Summary

| Test Suite Class | Focus Area | Assertions & Scenarios Tested |
| :--- | :--- | :--- |
| `AuthServiceTest.java` | Security & Auth | Valid login, salted password hash matching, invalid password rejection, duplicate email prevention, student registration. |
| `StudentServiceTest.java` | Student Management & Overloading | `getAllStudents()`, `searchStudent(name)`, `searchStudent(id)`, and `searchStudent(name, course)`. |
| `MenuServiceTest.java` | Menu CRUD & Stack Undo | Today's menu fetching, menu upsert, stack push, and `undoLastAction()` LIFO pop verification. |
| `AttendanceConcurrencyTest.java` | Multithreading & Synchronization | High-throughput concurrent thread simulation, `DuplicateMealAttendanceException` verification, mutual exclusion. |
| `ComplaintServiceTest.java` | Grievance Workflow | Complaint creation, status transition (`OPEN` → `RESOLVED`), and remarks assignment. |
| `BillingServiceTest.java` | Financial Calculation | Dynamic per-meal monthly rate calculation, dues summation, payment updates. |
| `ReportIOTest.java` | Java I/O Streams | Character streams export & read (`BufferedWriter`/`BufferedReader`), byte streams (`FileOutputStream`/`FileInputStream`). |
| `ArrayAnalyticsTest.java` | Array Algorithms | 1-D daily meal array sizing, 2-D Student × Meal matrix dimensions, and Jagged Array unequal month day allocations (28, 30, 31). |
| `ReflectionInspectorTest.java` | Java Reflection | Class metadata introspection, superclass resolution, interface checks, and declared field/method counts. |

---

## 3. How to Execute Tests
```bash
# Run all test suites via Maven
mvn test
```
