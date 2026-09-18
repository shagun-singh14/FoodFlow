# Java Syllabus & Concepts Implementation Mapping

This document provides a complete, 1-to-1 academic mapping between each topic in the **advanced Java curriculum** and its concrete, functional implementation in the **FoodFlow** codebase.

---

## Complete Concept Mapping Table

### 1. Java Fundamentals & Flow Control
- **Implementation**: Comprehensive input validation, formatted strings, control flow branches, and switch expressions.
- **Source Files**: [`ValidationUtil.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/util/ValidationUtil.java), [`AnalyticsService.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/service/AnalyticsService.java)

### 2. Classes and Objects
- **Implementation**: Encapsulated object-oriented domain representations for users, students, admins, menus, attendance records, feedback, complaints, staff, and bills.
- **Source Files**: [`Student.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/Student.java), [`Menu.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/Menu.java), [`Complaint.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/Complaint.java)

### 3. Methods and Constructors
- **Implementation**: Constructor overloading (default and parameterized), constructor chaining, helper methods, business mutators.
- **Source Files**: [`User.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/User.java), [`Student.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/Student.java)

### 4. Encapsulation
- **Implementation**: Strict `private` fields with validated `getters` and `setters`, defensive copies of date and collection objects.
- **Source Files**: [`User.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/User.java), [`MealAttendance.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/MealAttendance.java)

### 5. Inheritance
- **Implementation**: `User` is inherited by `Student` and `Admin`. `Report` is inherited by `StudentMealReport` and `MessAnalyticsReport`.
- **Source Files**: [`User.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/User.java) → [`Student.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/Student.java), [`Admin.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/Admin.java)

### 6. Polymorphism & Method Overriding
- **Implementation**: Abstract method `showDashboard()` overridden polymorphically in `Student` and `Admin`. Template method `generateBody()` overridden polymorphically in concrete reports.
- **Source Files**: [`User.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/User.java), [`Report.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/report/Report.java)

### 7. Abstract Classes
- **Implementation**: `public abstract class User` and `public abstract class Report` defining shared contracts, fields, and template logic.
- **Source Files**: [`User.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/User.java), [`Report.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/report/Report.java)

### 8. Interfaces
- **Implementation**: Custom interfaces defining contracts: `Authenticatable`, `Manageable`, `Reportable`, `Exportable`.
- **Source Files**: [`com/foodflow/model/interfaces/`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/interfaces/)

### 9. `this` and `super`
- **Implementation**: `this(...)` used for local constructor chaining; `super(...)` used in subclasses to initialize base `User` and `Report` states.
- **Source Files**: [`Student.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/Student.java), [`Admin.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/Admin.java)

### 10. `final` Keyword
- **Implementation**: `final` constants for date formatters and error codes; `final boolean authenticate(...)` method preventing sub-classes from bypassing password hashing.
- **Source Files**: [`User.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/User.java), [`DateUtil.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/util/DateUtil.java)

### 11. Enums
- **Implementation**: Type-safe enumerations with fields, constructor properties, and lookup methods.
- **Source Files**: `UserRole`, `MealType`, `ComplaintStatus`, `ComplaintCategory`, `PaymentStatus`, `FeedbackRating` in [`com/foodflow/model/enums/`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/enums/)

### 12. Nested / Inner Classes
- **Implementation**: 
  - Static Nested Class: `Menu.NutritionInfo` encapsulates caloric and macronutrient values.
  - Non-Static Inner Class: `ReportGenerator.ReportSummary` encapsulates file export outcome metadata.
- **Source Files**: [`Menu.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/model/Menu.java), [`ReportGenerator.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/report/ReportGenerator.java)

### 13. Method Overloading
- **Implementation**: Three distinct overloaded search signatures in `StudentService`:
  1. `searchStudent(String name)`
  2. `searchStudent(int id)`
  3. `searchStudent(String name, String course)`
- **Source Files**: [`StudentService.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/service/StudentService.java)

### 14. Singleton Pattern
- **Implementation**: Thread-safe double-checked locking singletons for `DatabaseManager`, `JPAUtil`, and `SessionManager`.
- **Source Files**: [`DatabaseManager.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/config/DatabaseManager.java), [`JPAUtil.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/config/JPAUtil.java), [`SessionManager.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/util/SessionManager.java)

### 15. Exception Handling & Custom Exceptions
- **Implementation**: Robust custom checked exception hierarchy inheriting `FoodFlowException`.
- **Source Files**: [`FoodFlowException.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/exception/FoodFlowException.java), [`DuplicateMealAttendanceException.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/exception/DuplicateMealAttendanceException.java), [`StudentNotEnrolledException.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/exception/StudentNotEnrolledException.java), etc.

### 16. Multithreading
- **Implementation**: `MealAttendanceTask` implements `Runnable` and `Callable<Boolean>`. `AttendanceSimulationManager` orchestrates concurrent threads via `ExecutorService`.
- **Source Files**: [`MealAttendanceTask.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/thread/MealAttendanceTask.java), [`AttendanceSimulationManager.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/thread/AttendanceSimulationManager.java)

### 17. Synchronization
- **Implementation**: `public synchronized MealAttendance markAttendanceSynchronized(...)` guarantees mutual exclusion and atomicity during high-throughput check-ins, preventing race-condition duplicate inserts.
- **Source Files**: [`AttendanceService.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/service/AttendanceService.java)

### 18. 1-D, 2-D, and Jagged Arrays
- **Implementation**:
  - **1-D Array (`int[4]`)**: Computes daily meal session attendance totals.
  - **2-D Array (`int[N][4]`)**: Matrix representing Student index × 4 Meal sessions attendance presence.
  - **Jagged Array (`int[12][]`)**: Multi-month trends where each of the 12 month sub-arrays has a distinct day count (28, 30, 31).
- **Source Files**: [`AnalyticsService.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/service/AnalyticsService.java)

### 19. Java Collections Framework
- **Implementation**:
  - **`ArrayList`**: Dynamic lists for menu rosters, students, and feedbacks.
  - **`Vector`**: Thread-safe student notification history collection.
  - **`Stack`**: LIFO administrative action history (`Stack<MenuAction>`) enabling the "Undo Last Action" feature.
- **Source Files**: [`MenuService.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/service/MenuService.java), [`NotificationService.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/service/NotificationService.java)

### 20. Java I/O Streams (Byte & Character Streams)
- **Implementation**:
  - **Character Streams**: `FileWriter`, `FileReader`, `BufferedReader`, `BufferedWriter` used for formatted `.txt` and `.csv` report export/reading.
  - **Byte Streams**: `FileOutputStream`, `FileInputStream`, `BufferedOutputStream` used for binary snapshot exports (`.dat`).
- **Source Files**: [`ReportGenerator.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/report/ReportGenerator.java)

### 21. JDBC (Java Database Connectivity)
- **Implementation**: Dedicated JDBC DAO layer using `Connection`, `PreparedStatement`, `Statement`, `ResultSet`, and `SQLException`.
- **Source Files**: [`StudentJDBCDAO.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/dao/StudentJDBCDAO.java), [`MenuJDBCDAO.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/dao/MenuJDBCDAO.java), [`AttendanceJDBCDAO.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/dao/AttendanceJDBCDAO.java)

### 22. JPA, Hibernate ORM & JPQL
- **Implementation**: Entity mapping (`@Entity`, `@Table`, `@Id`, `@OneToMany`, `@ManyToOne`, `@PrimaryKeyJoinColumn`) and typed JPQL aggregate queries.
- **Source Files**: [`com/foodflow/entity/`](file:///d:/FoodFlow/src/main/java/com/foodflow/entity/), [`FeedbackRepository.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/repository/FeedbackRepository.java)

### 23. Java Reflection
- **Implementation**: Runtime class introspection inspecting superclasses, implemented interfaces, declared fields, methods, constructors, and annotations.
- **Source Files**: [`ReflectionInspector.java`](file:///d:/FoodFlow/src/main/java/com/foodflow/util/ReflectionInspector.java)
