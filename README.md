# FoodFlow – College Mess Management System

[![Java Version](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://www.oracle.com/java/)
[![UI Framework](https://img.shields.io/badge/JavaFX-21.0.2-orange.svg)](https://openjfx.io/)
[![ORM Framework](https://img.shields.io/badge/Hibernate-6.4.4.Final-lightgrey.svg)](https://hibernate.org/)
[![Database](https://img.shields.io/badge/MySQL-8.0%2B-blue.svg)](https://www.mysql.com/)
[![Evaluation](https://img.shields.io/badge/VITyarthi-BYOP%20Project-success.svg)](https://vit.ac.in)

> **FoodFlow** is a complete, enterprise-grade, and academically rigorous software application developed for the **VITyarthi Build Your Own Project (BYOP) evaluation**. It digitizes student mess enrollment, daily 4-session menu scheduling, real-time concurrent attendance verification, food quality feedback, grievance redressal, and monthly invoice generation.

---

## 📑 Table of Contents
1. [Project Title & Overview](#1-project-title--overview)
2. [Problem Statement & Scope](#2-problem-statement--scope)
3. [Key Features](#3-key-features)
4. [Technologies & Tools Used](#4-technologies--tools-used)
5. [Java Syllabus & Concept Mapping](#5-java-syllabus--concept-mapping)
6. [System Architecture & Layering](#6-system-architecture--layering)
7. [Database Setup & Seed Data](#7-database-setup--seed-data)
8. [Step-by-Step Installation & Setup Guide](#8-step-by-step-installation--setup-guide)
9. [How to Run the Project (CLI & GUI)](#9-how-to-run-the-project-cli--gui)
10. [Instructions for Automated Testing](#10-instructions-for-automated-testing)
11. [Design Patterns & Concurrency Architecture](#11-design-patterns--concurrency-architecture)
12. [Project Executability & Evaluator Notes](#12-project-executability--evaluator-notes)

---

## 1. Project Title & Overview

**Project Title**: **FoodFlow – College Mess Management System**  
**Target Institution**: Vellore Institute of Technology (VIT)  
**Evaluation**: VITyarthi Build Your Own Project (BYOP)

FoodFlow transforms college dining hall operations by replacing error-prone manual paper registers and uncoordinated turnstiles with an automated, synchronized, and transparent software platform. It supports role-based access for **Students** and **Mess Administrators**, offering live menu nutrition tracking, 1-click meal attendance, grievance tracking, and automated monthly billing.

---

## 2. Problem Statement & Scope

### Problem Statement
In large residential universities, thousands of students dine across multiple mess halls daily. Traditional operations suffer from:
- **Food Wastage & Inaccurate Forecasting**: Caterers lack real-time data on expected student turnout.
- **Turnstile Concurrency Inconsistencies**: Peak check-in hours create race conditions that cause double-billing or unrecorded meals.
- **Unaddressed Grievances**: Complaints about food quality, hygiene, or timing get lost in manual suggestion boxes.
- **Billing Delays**: Manual tallying of daily meals into monthly dues causes errors and disputes.

### Scope of the Project
FoodFlow bridges students, kitchen staff, wardens, and mess administrators through a centralized desktop platform and terminal verification system. It covers the entire lifecycle of mess management: student profiles, daily menus, atomic attendance marking, star-rating feedback, grievance resolution, automated billing, and exportable audit reports.

---

## 3. Key Features

### 👤 Student Module
- **Secure Authentication**: Salted SHA-256 password hashing.
- **Daily Menu & Nutrition**: View Breakfast, Lunch, Snacks, Dinner with caloric and macronutrient values (Protein, Carbs, Fat).
- **Weekly Schedule**: Browse the full 7-day scheduled menu.
- **1-Click Meal Attendance**: Atomic attendance verification with duplicate check-in prevention.
- **Food Quality Feedback**: Submit 1-to-5 star ratings and detailed comments.
- **Grievance Redressal**: File complaints across 6 categories (Food Quality, Hygiene, Menu, Timing, Staff, Other) and track admin resolution status.
- **Billing Invoices**: View itemized monthly meal counts, computed dues, and payment statuses.
- **Notification Inbox**: Read announcements and dues alerts using `Vector` collection history.

### 🛡️ Mess Administrator Module
- **Operational KPI Overview**: Live cards showing Total Students, Today's Meals, Average Food Rating, Open Complaints, and Revenue.
- **Student Management (Overloaded Search)**: Multi-criteria student search by Name, numeric ID, or Name + Course (`searchStudent` overloading); toggle mess enrollment.
- **Menu Management (Stack-based Undo)**: Plan and update menus with a LIFO `Stack<MenuAction>` undo manager to revert mistaken actions.
- **Multithreaded Attendance Simulator**: Run concurrent worker thread simulations to verify thread safety and race condition elimination.
- **Grievance Resolution**: Update complaint statuses (`OPEN` → `IN_PROGRESS` → `RESOLVED` / `REJECTED`) with official admin remarks.
- **Billing & Payment Recording**: Dynamically generate monthly invoices based on attendance and mark payments as `PAID`.
- **Java I/O Reports Exporter**: Export formatted `.txt` (Character Streams: `BufferedWriter`), `.csv`, and binary snapshots `.dat` (Byte Streams: `FileOutputStream`).
- **Array Analytics Engine**: Compute 1-D daily session counts, 2-D Student × Meal attendance matrices, and 12-month Jagged Arrays for variable month day lengths.
- **Reflection Explorer**: Deep runtime introspection of class fields, methods, constructors, superclasses, and JPA annotations.

---

## 4. Technologies & Tools Used

| Category | Technologies / Tools | Purpose |
| :--- | :--- | :--- |
| **Programming Language** | **Java 17+ (JDK 17 / JDK 21 / JDK 26)** | Core logic, multithreading, OOP, Streams, Reflection |
| **User Interface** | **JavaFX 21.0.2** with Custom Glassmorphic CSS | Modern, responsive desktop GUI |
| **Direct Persistence** | **JDBC** (`PreparedStatement`, `ResultSet`, Transactions) | High-speed transactional data access |
| **ORM Framework** | **Jakarta Persistence (JPA 3.1) / Hibernate 6.4** | Object-Relational Mapping & JPQL queries |
| **Primary Database** | **MySQL 8.0+** (`foodflow_db`) | Production relational database storage |
| **Zero-Config Database** | **Embedded H2 In-Memory DB** | Automatic fallback enabling instant evaluation with zero setup |
| **Build & Dependencies** | **Apache Maven 3.9+** | Project build and dependency management |
| **Testing Framework** | **JUnit 5 Jupiter** | Automated unit and integration testing |
| **Version Control** | **Git / GitHub** | Repository and source code management |

---

## 5. Java Syllabus & Concept Mapping

| Java Syllabus Topic | Concrete Implementation in FoodFlow | Source File Reference |
| :--- | :--- | :--- |
| **Abstract Classes & Interfaces** | `User` and `Report` abstract base classes; `Authenticatable`, `Manageable`, `Reportable`, `Exportable` interfaces | [`User.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/User.java), [`Report.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/report/Report.java) |
| **Inheritance & Polymorphism** | Base `User` inherited by `Student` and `Admin`; polymorphic dispatch in `showDashboard()` | [`Student.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/Student.java), [`Admin.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/Admin.java) |
| **`this`, `super`, `final`** | Parameterized constructor chaining with `this(...)`, `super(...)`, and `final boolean authenticate(...)` | [`User.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/User.java) |
| **Type-Safe Enums** | `UserRole`, `MealType`, `ComplaintStatus`, `ComplaintCategory`, `PaymentStatus`, `FeedbackRating` | [`com/foodflow/model/enums/`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/enums/) |
| **Nested / Inner Classes** | Static nested class `Menu.NutritionInfo`; non-static inner class `ReportGenerator.ReportSummary` | [`Menu.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/Menu.java), [`ReportGenerator.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/report/ReportGenerator.java) |
| **Method Overloading** | Overloaded search methods: `searchStudent(String name)`, `searchStudent(int id)`, `searchStudent(String name, String course)` | [`StudentService.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/service/StudentService.java) |
| **Collections: Stack Undo** | LIFO `Stack<MenuAction>` for administrative "Undo Last Action" feature | [`MenuService.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/service/MenuService.java) |
| **Collections: Vector & List** | `Vector<Notification>` for student notification history; `ArrayList` for rosters and menus | [`NotificationService.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/service/NotificationService.java) |
| **1-D, 2-D & Jagged Arrays** | 1-D daily session counts `int[4]`; 2-D Student × Meal matrix `int[N][4]`; 12-month Jagged Array `int[12][]` with unequal month lengths (28, 30, 31) | [`AnalyticsService.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/service/AnalyticsService.java) |
| **Multithreading & Sync** | `MealAttendanceTask implements Runnable, Callable`; thread pool simulator; `public synchronized void markAttendanceSynchronized(...)` preventing race conditions | [`AttendanceService.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/service/AttendanceService.java), [`AttendanceSimulationManager.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/thread/AttendanceSimulationManager.java) |
| **Java I/O Streams** | Character streams (`FileWriter`, `FileReader`, `BufferedReader`, `BufferedWriter`); Byte streams (`FileOutputStream`, `FileInputStream`) for `.txt`, `.csv`, `.dat` | [`ReportGenerator.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/report/ReportGenerator.java) |
| **JDBC & JPA / Hibernate** | Dedicated JDBC DAO layer (`PreparedStatement`, `ResultSet`) + JPA Entities & Repositories (`@Entity`, `@OneToMany`, JPQL) | [`com/foodflow/dao/`](file:///d:/FoodFlow/src/main/java/com/foodflow/dao/), [`com/foodflow/repository/`](file:///d:/FoodFlow/src/main/java/com/foodflow/repository/) |
| **Java Reflection** | Runtime introspection tool exploring entity superclasses, interfaces, fields, methods, constructors, and annotations | [`ReflectionInspector.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/util/ReflectionInspector.java) |
| **Custom Exceptions** | `DuplicateMealAttendanceException`, `StudentNotEnrolledException`, `InvalidMealException`, `ComplaintNotFoundException`, `DatabaseException`, etc. | [`com/foodflow/exception/`](file:///d:/FoodFlow/src/main/java/com/foodflow/exception/) |
| **Singletons** | Double-checked locking thread-safe singletons: `DatabaseManager`, `JPAUtil`, `SessionManager` | [`com/foodflow/config/`](file:///d:/FoodFlow/src/main/java/com/foodflow/config/) |

---

## 6. System Architecture & Layering

```text
+-------------------------------------------------------------+
|                     1. Presentation Layer                   |
|   JavaFX Desktop Views, Controllers, Glassmorphic CSS Theme |
+-------------------------------------------------------------+
                               |
                               v
+-------------------------------------------------------------+
|                      2. Service Layer                       |
|   Business Logic, Overloaded Methods, Synchronization,      |
|   Stack Undo Management, Array Computations, Session State  |
+-------------------------------------------------------------+
                               |
                               v
+-------------------------------------------------------------+
|              3. Persistence Abstraction Layer               |
|      JDBC DAO Layer          |      JPA / ORM Repositories  |
|      (StudentJDBCDAO, etc.)  |      (StudentRepository)     |
+-------------------------------------------------------------+
                               |
                               v
+-------------------------------------------------------------+
|                  4. Database Drivers & Pool                 |
|       MySQL Connector/J      |      Hibernate ORM 6.4       |
|       DatabaseManager        |      JPAUtil (EMF Singleton) |
+-------------------------------------------------------------+
                               |
                               v
+-------------------------------------------------------------+
|                       5. Data Storage                       |
|      MySQL 8.0+ Database     |      Embedded H2 (Fallback)  |
+-------------------------------------------------------------+
```

---

## 7. Database Setup & Seed Data

The database schema and sample data are defined in [`sql/foodflow_database.sql`](file:///d:/FoodFlow/sql/foodflow_database.sql).

### MySQL Setup (Optional):
```bash
mysql -u root -p < sql/foodflow_database.sql
```
Adjust credentials in `src/main/resources/application.properties` if needed:
```properties
db.username=root
db.password=your_password
```

> **Zero-Config Out-of-the-Box Fallback**: If MySQL is not running on the evaluator's machine, FoodFlow automatically boots up with its built-in embedded in-memory database pre-loaded with all seed records (10 students, 2 admins, 7 days menus, attendance, feedback, complaints, and bills). No manual database setup is strictly required!

### Pre-configured Seed Credentials:
- **Admin**: `admin@foodflow.edu` / `Password@123`
- **Student**: `rahul.sharma@vit.ac.in` / `Password@123`

---

## 8. Step-by-Step Installation & Setup Guide

### Step 1: Clone the Repository
```bash
git clone https://github.com/your-username/FoodFlow.git
cd FoodFlow
```

### Step 2: Verify Java JDK
Ensure Java 17 or later is installed:
```bash
java -version
```

### Step 3: Build the Project
Using Maven:
```bash
mvn clean compile
```

---

## 9. How to Run the Project (CLI & GUI)

### Option A: Running in Terminal / Headless CLI Environment (Evaluator Recommended)
To run the complete automated end-to-end programmatic verification demonstrating all 38 syllabus topics from the command line:

**Using Maven**:
```bash
mvn exec:java -Dexec.mainClass="com.foodflow.DemoRunner"
```

**Using the included Batch Runner**:
```cmd
run-demo.bat
```

**Using Direct Java Command**:
```powershell
$jars = (Get-ChildItem -Path "$HOME\.m2\repository" -Filter "*.jar" -Recurse | Select-Object -ExpandProperty FullName) -join ";"
java -cp "target\classes;$jars" com.foodflow.DemoRunner
```

---

### Option B: Running the JavaFX Desktop GUI Application
To launch the modern desktop user interface:

**Using Maven**:
```bash
mvn javafx:run
```

**Using the included Batch Runner**:
```cmd
run-app.bat
```

**Using Direct Java Command**:
```powershell
$jars = (Get-ChildItem -Path "$HOME\.m2\repository" -Filter "*.jar" -Recurse | Select-Object -ExpandProperty FullName) -join ";"
java -cp "target\classes;$jars" com.foodflow.Main
```

**In IDE (IntelliJ IDEA / Eclipse / VS Code)**:
- Open [`Main.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/Main.java) and click **Run**.

---

## 10. Instructions for Automated Testing

To run the complete JUnit 5 automated test suite covering authentication, method overloading, stack undo, concurrency synchronization, complaints, billing, array analytics, Java I/O streams, and reflection:

**Using Maven**:
```bash
mvn test
```

**Using the Direct Test Harness**:
```powershell
$jars = (Get-ChildItem -Path "$HOME\.m2\repository" -Filter "*.jar" -Recurse | Select-Object -ExpandProperty FullName) -join ";"
java -cp "target\classes;target\test-classes;$jars" com.foodflow.TestRunner
```

### Expected Test Output:
```text
================================================================================
   FOODFLOW JUNIT 5 TEST EXECUTION HARNESS                                      
================================================================================

  ✓ [PASS] AuthServiceTest (4 tests)
  ✓ [PASS] StudentServiceTest (4 tests - Method Overloading)
  ✓ [PASS] MenuServiceTest (2 tests - Stack Undo)
  ✓ [PASS] AttendanceConcurrencyTest (2 tests - Multithreading & Synchronization)
  ✓ [PASS] ComplaintServiceTest (1 test)
  ✓ [PASS] BillingServiceTest (2 tests)
  ✓ [PASS] ReportIOTest (2 tests - Char & Byte Streams)
  ✓ [PASS] ArrayAnalyticsTest (3 tests - 1D, 2D & Jagged Arrays)
  ✓ [PASS] ReflectionInspectorTest (1 test - Java Reflection)

--------------------------------------------------------------------------------
 TEST RESULTS: 21 PASSED | 0 FAILED
 STATUS      : ALL TESTS PASSED SUCCESSFULLY! (100%)
--------------------------------------------------------------------------------
```

---

## 11. Design Patterns & Concurrency Architecture

1. **Singleton Pattern**: Thread-safe double-checked locking in `DatabaseManager`, `JPAUtil`, and `SessionManager`.
2. **Template Method Pattern**: `Report.java` defining the standard header/footer formatting workflow while subclasses implement custom report bodies.
3. **DAO & Repository Pattern**: Clean separation between database drivers and business services.
4. **Thread Synchronization**: `public synchronized MealAttendance markAttendanceSynchronized(...)` guarantees mutual exclusion and atomicity during high-throughput check-ins, preventing race-condition duplicate inserts.

---

## 12. Project Executability & Evaluator Notes

- **Zero External Dependencies Required to Evaluate**: The application includes an embedded in-memory database fallback so that it can be evaluated instantly without configuring a local MySQL server.
- **Headless Terminal Support**: `com.foodflow.DemoRunner` and `com.foodflow.TestRunner` allow evaluators in terminal/headless environments to inspect all functionalities without requiring a GUI display server.
- **Production Code Standards**: All code adheres to clean OOP principles, comprehensive exception handling, salted security hashing, and structured documentation.

---

## 13. License & Author
- **Project**: FoodFlow – College Mess Management System
- **Evaluation**: VITyarthi Build Your Own Project (BYOP)
- **License**: MIT Academic License
