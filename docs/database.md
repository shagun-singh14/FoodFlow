# Database Design Documentation

## Database Name: `foodflow_db`
Compatible with MySQL 8.0+ / MariaDB / Embedded H2.

---

## 1. Schema & Relational Tables

### `users`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique user identifier |
| `name` | VARCHAR(100) | NOT NULL | Full name of user |
| `email` | VARCHAR(100) | NOT NULL, UNIQUE | University institutional email |
| `phone` | VARCHAR(20) | NOT NULL | Contact number |
| `password_hash` | VARCHAR(255) | NOT NULL | Salted SHA-256 hash |
| `password_salt` | VARCHAR(64) | NOT NULL | 16-byte random salt (Base64) |
| `role` | VARCHAR(20) | NOT NULL, DEFAULT 'STUDENT' | Security role (STUDENT / ADMIN) |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Account creation time |
| `updated_at` | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | Last modification time |

### `students`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `student_id` | BIGINT | PRIMARY KEY, FK -> `users(id)` | Foreign key joining base user |
| `registration_number` | VARCHAR(30) | NOT NULL, UNIQUE | Academic Reg No (e.g. 23BCE1001) |
| `course` | VARCHAR(50) | NOT NULL | Academic branch |
| `year` | INT | NOT NULL | Year of study (1 to 4) |
| `hostel` | VARCHAR(50) | NOT NULL | Hostel block name |
| `room_number` | VARCHAR(20) | NOT NULL | Assigned room |
| `is_enrolled` | BOOLEAN | NOT NULL, DEFAULT TRUE | Mess plan enrollment status |

### `menus`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `menu_id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Menu identifier |
| `menu_date` | DATE | NOT NULL, UNIQUE | Calendar date of menu |
| `breakfast` | TEXT | NOT NULL | Breakfast items |
| `lunch` | TEXT | NOT NULL | Lunch items |
| `snacks` | TEXT | NOT NULL | Evening snacks items |
| `dinner` | TEXT | NOT NULL | Dinner items |
| `calories` | INT | DEFAULT 2200 | Total daily calories (kcal) |
| `protein_grams` | DOUBLE | DEFAULT 75.0 | Total protein (g) |
| `carbs_grams` | DOUBLE | DEFAULT 280.0 | Total carbohydrates (g) |
| `fat_grams` | DOUBLE | DEFAULT 60.0 | Total fat (g) |

### `meal_attendance`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `attendance_id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Attendance record ID |
| `student_id` | BIGINT | NOT NULL, FK -> `students` | Enrolled student ID |
| `attendance_date` | DATE | NOT NULL | Date of meal |
| `meal_type` | VARCHAR(20) | NOT NULL | BREAKFAST / LUNCH / SNACKS / DINNER |
| `status` | VARCHAR(20) | NOT NULL, DEFAULT 'PRESENT'| Attendance status |
| `marked_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Timestamp of check-in |
| **Unique Key** | (student_id, attendance_date, meal_type) | Enforces 1 check-in per meal session |

### `feedbacks`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `feedback_id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Feedback ID |
| `student_id` | BIGINT | NOT NULL, FK -> `students` | Reviewing student ID |
| `meal_type` | VARCHAR(20) | NOT NULL | Meal session |
| `rating` | INT | NOT NULL, CHECK (1..5) | 1 to 5 star rating |
| `comments` | TEXT | NULL | Detailed feedback commentary |
| `feedback_date` | DATE | NOT NULL | Review date |

### `complaints`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `complaint_id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Complaint ticket ID |
| `student_id` | BIGINT | NOT NULL, FK -> `students` | Filing student ID |
| `category` | VARCHAR(50) | NOT NULL | FOOD_QUALITY / HYGIENE / MENU / TIMING / STAFF |
| `description` | TEXT | NOT NULL | Grievance details |
| `status` | VARCHAR(20) | NOT NULL, DEFAULT 'OPEN' | OPEN / IN_PROGRESS / RESOLVED / REJECTED |
| `admin_remarks` | TEXT | NULL | Resolution notes |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Filed timestamp |
| `resolved_at` | TIMESTAMP | NULL | Resolution timestamp |

### `bills`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `bill_id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Invoice number |
| `student_id` | BIGINT | NOT NULL, FK -> `students` | Student ID |
| `billing_month` | VARCHAR(20) | NOT NULL | Billing period (e.g. September 2026) |
| `total_meals` | INT | NOT NULL, DEFAULT 0 | Total count of meals consumed |
| `amount` | DECIMAL(10,2)| NOT NULL, DEFAULT 0.00 | Total calculated dues |
| `payment_status`| VARCHAR(20) | NOT NULL, DEFAULT 'PENDING'| PENDING / PAID / OVERDUE |
| `paid_at` | TIMESTAMP | NULL | Payment timestamp |

---

## 2. Relational Integrity & Performance Optimization
- **Cascading Foreign Keys**: `ON DELETE CASCADE` ensures student records maintain referential integrity.
- **Indexes**: Explicit B-Tree indexes created on `email`, `registration_number`, `attendance_date`, `menu_date`, and `payment_status` for sub-millisecond query lookups.
