# Problem Statement & System Specification

## Project Title
**FoodFlow – College Mess Management System**

---

## 1. Problem Statement
In large residential collegiate campuses, dining halls cater to tens of thousands of residential students across multiple hostel blocks every single day. Traditional mess management methodologies rely on disconnected manual ledgers, isolated turnstiles, or rudimentary spreadsheets. These obsolete approaches introduce severe operational friction:

1. **Unchecked Food Wastage & Lack of Demand Forecasting**: Mess caterers lack real-time predictive data on attendance, causing massive over-preparation or sudden shortages.
2. **Double-Tapping and Concurrency Inconsistencies**: Peak meal check-ins generate race conditions at RFID turnstiles and app gateways, creating duplicate billing entries or unrecorded meals.
3. **Ineffective Grievance Redressal**: Student complaints regarding food hygiene, quality, or dietary preferences are often lost in paper suggestion boxes without tracking or audit trails.
4. **Billing Inaccuracies**: Manual compilation of monthly per-meal billing statements leads to discrepancies, delays in dues collection, and lack of transparency.
5. **Absence of Nutritional and Quality Analytics**: Students lack visibility into daily caloric and macronutrient breakdowns, while admins cannot track low-rated dishes to enforce vendor accountability.

---

## 2. Scope of the Project
**FoodFlow** is an enterprise-grade management platform engineered in Java 17+ and JavaFX, featuring dual-persistence (JDBC and JPA/Hibernate), thread synchronization, and complete terminal/GUI executability. It establishes an end-to-end digital pipeline bridging students, mess staff, wardens, and administrative caterers.

The project scope encompasses:
- **Student Profile & Enrollment Lifecycle**: Digital onboarding, hostel room assignment, and active mess plan management.
- **Daily 4-Session Menu Planning**: Caloric and macronutrient breakdowns (Breakfast, Lunch, Snacks, Dinner) with Stack-based Undo.
- **Atomic Meal Attendance**: High-concurrency synchronization preventing duplicate check-ins and race conditions.
- **Grievance Redressal**: Multi-category complaint lifecycle (`OPEN` → `IN_PROGRESS` → `RESOLVED` / `REJECTED`).
- **Feedback & Quality Governance**: 1-to-5 star rating analytics with automated average rating computations.
- **Dynamic Billing & Invoicing**: Automated per-meal rate calculation and payment settlement tracking.
- **Analytical Reporting & Portability**: Exportable character and byte streams (`.txt`, `.csv`, `.dat`).

---

## 3. Target Users & Stakeholders

| User Role | Responsibilities & Capabilities |
| :--- | :--- |
| **Residential Students** | View daily & weekly menus with nutrition info; mark real-time meal attendance; file categorized complaints with status tracking; submit star-ratings & reviews; monitor monthly invoices and payment status; view announcements via notification inbox. |
| **Mess Administrators & Wardens** | Schedule menus with Stack-based Undo history; inspect student profiles via overloaded search filters; monitor concurrent turnstile simulation; manage mess staff; track low-rated meals; resolve complaints; export byte & character stream reports. |
| **Catering Operations Team** | View 1-D daily meal volume metrics, 2-D Student × Meal attendance matrices, and multi-month jagged attendance trends to forecast food procurement. |

---

## 4. High-Level Features

1. **Student Management & Overloaded Search**:
   - Register, authenticate, and toggle enrollment status.
   - Multi-criteria overloaded search: `searchStudent(String name)`, `searchStudent(int id)`, `searchStudent(String name, String course)`.
2. **Menu Planning with Stack Undo**:
   - 4-session daily menu scheduling with caloric/macronutrient breakdown.
   - Administrative LIFO `Stack<MenuAction>` undo manager to revert additions, updates, or deletions.
3. **Synchronized Meal Attendance**:
   - Thread-safe atomic check-ins using Java object monitors (`synchronized markAttendanceSynchronized`).
   - Custom exceptions: `DuplicateMealAttendanceException`, `StudentNotEnrolledException`, `InvalidMealException`.
4. **Multithreaded Turnstile Simulation**:
   - `MealAttendanceTask` (`Runnable`/`Callable`) orchestrated via `ExecutorService` thread pool to simulate peak-hour load.
5. **Grievance Redressal & Resolution**:
   - Categorized complaint ticketing (Food Quality, Hygiene, Menu, Timing, Staff, Other) with admin remarks.
6. **Food Quality & Rating Analytics**:
   - 1-to-5 star rating system with aggregate ratings calculation and low-rated dish detection.
7. **Automated Monthly Billing**:
   - Dynamic per-meal rate accounting (Breakfast: ₹35, Lunch: ₹50, Snacks: ₹25, Dinner: ₹45) and payment reconciliation.
8. **Java I/O Stream Exporting**:
   - Character streams (`BufferedWriter`, `FileReader`) and byte streams (`FileOutputStream`, `FileInputStream`) for `.txt`, `.csv`, and `.dat` formats.
9. **Array Analytics Engine**:
   - 1-D daily meal array `int[4]`, 2-D Student × Meal matrix `int[N][4]`, and 12-month Jagged Array `int[12][]` with variable day allocations.
10. **System Reflection Explorer**:
    - Runtime class introspection exploring superclasses, interfaces, declared fields, methods, constructors, and JPA annotations.

---

## 5. Expected Outcomes
The project achieves:
- Complete compliance with all project functional and architectural requirement specifications.
- Full command-line and GUI executability with zero-config database fallback.
- Complete JUnit 5 test suite coverage passing with 100% success rate.
- Production-grade code quality, modular architecture, salted password security, and thorough technical documentation.
