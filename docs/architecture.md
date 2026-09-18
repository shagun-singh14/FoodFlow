# System Architecture Documentation

## 1. Architectural Style: Layered Architecture
FoodFlow adheres to a strict, decoupled 5-tier layered architecture:

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

## 2. Separation of Concerns
1. **Presentation Layer (`com.foodflow.view`)**:
   - Manages UI controls, user inputs, responsive layouts, tab panes, modal dialogs, and styling.
   - Delegates all business logic and persistence calls to the Service layer.

2. **Service Layer (`com.foodflow.service`)**:
   - Contains all validation, transactional boundaries, thread synchronization, calculation algorithms, and Stack undo history.
   - Does not touch raw UI controls or database SQL directly.

3. **DAO & Repository Layer (`com.foodflow.dao`, `com.foodflow.repository`)**:
   - Direct JDBC DAOs execute SQL queries using `PreparedStatements` and map `ResultSet` rows to domain models.
   - JPA Repositories leverage `EntityManager` and JPQL for object-relational mapping.

4. **Configuration & Utilities (`com.foodflow.config`, `com.foodflow.util`)**:
   - Singleton connection managers (`DatabaseManager`, `JPAUtil`, `SessionManager`).
   - Logging, reflection inspection, date formatting, and salted password hashing.

---

## 3. High Concurrency & Thread Synchronization
- Concurrency during meal check-in windows is handled via Java thread monitors (`synchronized markAttendanceSynchronized`).
- Background report writing and file exports are executed on non-blocking worker threads to maintain UI responsiveness.
