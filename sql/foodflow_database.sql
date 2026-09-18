-- =====================================================================
-- FoodFlow – College Mess Management System Database Script
-- Compatible with MySQL 8.0+, MariaDB, and H2 (Embedded Mode)
-- Database: foodflow_db
-- =====================================================================

DROP DATABASE IF EXISTS foodflow_db;
CREATE DATABASE foodflow_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE foodflow_db;

-- ---------------------------------------------------------------------
-- 1. Table: users (Base table for Student and Admin accounts)
-- ---------------------------------------------------------------------
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    password_salt VARCHAR(64) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------
-- 2. Table: students (Extends user with academic & hostel details)
-- ---------------------------------------------------------------------
CREATE TABLE students (
    student_id BIGINT PRIMARY KEY,
    registration_number VARCHAR(30) NOT NULL UNIQUE,
    course VARCHAR(50) NOT NULL,
    academic_year INT NOT NULL,
    hostel VARCHAR(50) NOT NULL,
    room_number VARCHAR(20) NOT NULL,
    is_enrolled BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- 3. Table: mess_staff (Operational staff records)
-- ---------------------------------------------------------------------
CREATE TABLE mess_staff (
    staff_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    shift VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------
-- 4. Table: menus (Daily meal schedules with nutritional information)
-- ---------------------------------------------------------------------
CREATE TABLE menus (
    menu_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    menu_date DATE NOT NULL UNIQUE,
    breakfast TEXT NOT NULL,
    lunch TEXT NOT NULL,
    snacks TEXT NOT NULL,
    dinner TEXT NOT NULL,
    calories INT DEFAULT 2200,
    protein_grams DOUBLE DEFAULT 75.0,
    carbs_grams DOUBLE DEFAULT 280.0,
    fat_grams DOUBLE DEFAULT 60.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------
-- 5. Table: meal_attendance (Tracks daily student meal attendance)
-- ---------------------------------------------------------------------
CREATE TABLE meal_attendance (
    attendance_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    meal_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PRESENT',
    marked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_student_date_meal UNIQUE (student_id, attendance_date, meal_type),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- 6. Table: feedbacks (Meal ratings and comments)
-- ---------------------------------------------------------------------
CREATE TABLE feedbacks (
    feedback_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    meal_type VARCHAR(20) NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comments TEXT,
    feedback_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- 7. Table: complaints (Grievances filed by students)
-- ---------------------------------------------------------------------
CREATE TABLE complaints (
    complaint_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    admin_remarks TEXT,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- 8. Table: bills (Monthly student meal charges)
-- ---------------------------------------------------------------------
CREATE TABLE bills (
    bill_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    billing_month VARCHAR(20) NOT NULL,
    total_meals INT NOT NULL DEFAULT 0,
    amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    paid_at TIMESTAMP NULL,
    CONSTRAINT uq_student_month UNIQUE (student_id, billing_month),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- 9. Table: notifications (Student announcements & alerts)
-- ---------------------------------------------------------------------
CREATE TABLE notifications (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    notification_date DATE NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- Standalone Indexes
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_role ON users(role);
CREATE INDEX idx_student_reg ON students(registration_number);
CREATE INDEX idx_student_hostel ON students(hostel);
CREATE INDEX idx_menu_date ON menus(menu_date);
CREATE INDEX idx_attendance_date ON meal_attendance(attendance_date);
CREATE INDEX idx_attendance_student ON meal_attendance(student_id);
CREATE INDEX idx_feedback_date ON feedbacks(feedback_date);
CREATE INDEX idx_feedback_meal ON feedbacks(meal_type);
CREATE INDEX idx_complaint_status ON complaints(status);
CREATE INDEX idx_complaint_student ON complaints(student_id);
CREATE INDEX idx_bill_student ON bills(student_id);
CREATE INDEX idx_bill_month ON bills(billing_month);
CREATE INDEX idx_notif_student ON notifications(student_id);


-- =====================================================================
-- SEED DATA INSERTION
-- Password for all seed users is: "Password@123"
-- Salt: "foodflow_salt_2026"
-- Hash: Generated via SHA-256(salt + password)
-- =====================================================================

-- 1. Insert Users (2 Admins, 10 Students)
INSERT INTO users (id, name, email, phone, password_hash, password_salt, role) VALUES
(1, 'Prof. Arvind Menon', 'admin@foodflow.edu', '9876543210', 'b6f6981cf6ae3baebfc2efadbaad5fdb0e34c9c7df620023ee45ba5dffea59dc', 'foodflow_salt_2026', 'ADMIN'),
(2, 'Dr. Sunita Rao', 'sunita.admin@foodflow.edu', '9876543211', 'b6f6981cf6ae3baebfc2efadbaad5fdb0e34c9c7df620023ee45ba5dffea59dc', 'foodflow_salt_2026', 'ADMIN'),
(3, 'Rahul Sharma', 'rahul.sharma@vit.ac.in', '9123456701', 'b6f6981cf6ae3baebfc2efadbaad5fdb0e34c9c7df620023ee45ba5dffea59dc', 'foodflow_salt_2026', 'STUDENT'),
(4, 'Ananya Patel', 'ananya.patel@vit.ac.in', '9123456702', 'b6f6981cf6ae3baebfc2efadbaad5fdb0e34c9c7df620023ee45ba5dffea59dc', 'foodflow_salt_2026', 'STUDENT'),
(5, 'Rohan Verma', 'rohan.verma@vit.ac.in', '9123456703', 'b6f6981cf6ae3baebfc2efadbaad5fdb0e34c9c7df620023ee45ba5dffea59dc', 'foodflow_salt_2026', 'STUDENT'),
(6, 'Priya Nair', 'priya.nair@vit.ac.in', '9123456704', 'b6f6981cf6ae3baebfc2efadbaad5fdb0e34c9c7df620023ee45ba5dffea59dc', 'foodflow_salt_2026', 'STUDENT'),
(7, 'Aditya Joshi', 'aditya.joshi@vit.ac.in', '9123456705', 'b6f6981cf6ae3baebfc2efadbaad5fdb0e34c9c7df620023ee45ba5dffea59dc', 'foodflow_salt_2026', 'STUDENT'),
(8, 'Sneha Kulkarni', 'sneha.k@vit.ac.in', '9123456706', 'b6f6981cf6ae3baebfc2efadbaad5fdb0e34c9c7df620023ee45ba5dffea59dc', 'foodflow_salt_2026', 'STUDENT'),
(9, 'Vikram Singh', 'vikram.singh@vit.ac.in', '9123456707', 'b6f6981cf6ae3baebfc2efadbaad5fdb0e34c9c7df620023ee45ba5dffea59dc', 'foodflow_salt_2026', 'STUDENT'),
(10, 'Divya Ramesh', 'divya.ramesh@vit.ac.in', '9123456708', 'b6f6981cf6ae3baebfc2efadbaad5fdb0e34c9c7df620023ee45ba5dffea59dc', 'foodflow_salt_2026', 'STUDENT'),
(11, 'Karthik Raja', 'karthik.raja@vit.ac.in', '9123456709', 'b6f6981cf6ae3baebfc2efadbaad5fdb0e34c9c7df620023ee45ba5dffea59dc', 'foodflow_salt_2026', 'STUDENT'),
(12, 'Meera Iyer', 'meera.iyer@vit.ac.in', '9123456710', 'b6f6981cf6ae3baebfc2efadbaad5fdb0e34c9c7df620023ee45ba5dffea59dc', 'foodflow_salt_2026', 'STUDENT');

-- 2. Insert Student Profiles
INSERT INTO students (student_id, registration_number, course, academic_year, hostel, room_number, is_enrolled) VALUES
(3, '23BCE1001', 'B.Tech CSE', 3, 'Block A - Mens Hostel', 'A-304', TRUE),
(4, '23BCE1045', 'B.Tech CSE', 3, 'Block D - Ladies Hostel', 'D-112', TRUE),
(5, '24BIT1012', 'B.Tech IT', 2, 'Block B - Mens Hostel', 'B-205', TRUE),
(6, '24BEC1088', 'B.Tech ECE', 2, 'Block E - Ladies Hostel', 'E-401', TRUE),
(7, '22BME1034', 'B.Tech Mech', 4, 'Block C - Mens Hostel', 'C-108', TRUE),
(8, '23BCE1190', 'B.Tech CSE', 3, 'Block D - Ladies Hostel', 'D-220', TRUE),
(9, '25BCE2004', 'B.Tech CSE', 1, 'Block A - Mens Hostel', 'A-102', TRUE),
(10, '24BDS1015', 'B.Tech Data Sci', 2, 'Block E - Ladies Hostel', 'E-315', TRUE),
(11, '23BIT1099', 'B.Tech IT', 3, 'Block B - Mens Hostel', 'B-412', TRUE),
(12, '22BEE1021', 'B.Tech EEE', 4, 'Block D - Ladies Hostel', 'D-305', TRUE);

-- 3. Insert Mess Staff
INSERT INTO mess_staff (staff_id, name, role, phone, shift) VALUES
(1, 'Ramesh Babu', 'Head Chef', '9840112233', 'MORNING'),
(2, 'Govind Swamy', 'Assistant Chef', '9840112234', 'EVENING'),
(3, 'Manoj Kumar', 'Hygiene Supervisor', '9840112235', 'FULL_DAY'),
(4, 'Suresh Reddy', 'Store Manager', '9840112236', 'MORNING'),
(5, 'Lakshmi Amma', 'Dining Staff Lead', '9840112237', 'EVENING');

-- 4. Insert 7 Days of Menus
INSERT INTO menus (menu_date, breakfast, lunch, snacks, dinner, calories, protein_grams, carbs_grams, fat_grams) VALUES
('2026-09-15', 'Masala Dosa, Sambar, Coconut Chutney, Tea/Coffee', 'Veg Biryani, Paneer Butter Masala, Raita, Gulab Jamun', 'Pani Puri, Masala Chai', 'Phulka, Dal Makhani, Jeera Rice, Curd', 2250, 78.0, 290.0, 58.0),
('2026-09-16', 'Idli, Vada, Tomato Chutney, Filter Coffee', 'Steamed Rice, Rasam, Aloo Gobi, Papad, Payasam', 'Samosa, Mint Chutney, Tea', 'Chapati, Mix Veg Curry, Lemon Rice, Buttermilk', 2180, 72.0, 275.0, 55.0),
('2026-09-17', 'Poori Bhaji, Sprouts Salad, Hot Bournvita', 'North Indian Thali: Shahi Paneer, Dal Tadka, Naan, Rice', 'Veg Cutlet, Tomato Ketchup, Green Tea', 'Rotis, Chana Masala, Veg Pulao, Fruit Custard', 2300, 82.0, 310.0, 62.0),
('2026-09-18', 'Poha, Sev, Boiled Eggs / Banana, Filter Coffee', 'South Indian Meals: Sambar, Kootu, Poriyal, Curd Rice', 'Bhel Puri, Ginger Chai', 'Butter Naan, Kadai Paneer, Veg Dum Biryani, Ice Cream', 2350, 85.0, 305.0, 65.0),
('2026-09-19', 'Aloo Paratha, Curd, Pickle, Masala Tea', 'Rajma Chawal, Boondi Raita, Roasted Papad, Salad', 'Mirchi Bajji, Filter Coffee', 'Tandoori Roti, Dal Fry, Kashmiri Pulao, Rasgulla', 2220, 76.0, 285.0, 60.0),
('2026-09-20', 'Upma, Coconut Chutney, Boiled Sweet Corn, Coffee', 'Veg Fried Rice, Gobi Manchurian, Sweet Corn Soup', 'Pav Bhaji, Lemonade', 'Parotta, Veg Kurma, Ghee Rice, Moong Dal Halwa', 2400, 80.0, 320.0, 68.0),
('2026-09-21', 'Uttapam, Sambar, Onion Chutney, Hot Chocolate', 'Methi Paratha, Paneer Bhurji, Dal Panchmel, Rice', 'Dhokla, Green Chutney, Tea', 'Phulka, Mushroom Mutter, Veg Biryani, Fruit Salad', 2210, 79.0, 280.0, 56.0);

-- 5. Insert Meal Attendance History
INSERT INTO meal_attendance (student_id, attendance_date, meal_type, status) VALUES
(3, '2026-09-18', 'BREAKFAST', 'PRESENT'),
(3, '2026-09-18', 'LUNCH', 'PRESENT'),
(4, '2026-09-18', 'BREAKFAST', 'PRESENT'),
(4, '2026-09-18', 'LUNCH', 'PRESENT'),
(4, '2026-09-18', 'SNACKS', 'PRESENT'),
(5, '2026-09-18', 'BREAKFAST', 'PRESENT'),
(6, '2026-09-18', 'BREAKFAST', 'PRESENT'),
(6, '2026-09-18', 'LUNCH', 'PRESENT'),
(7, '2026-09-18', 'LUNCH', 'PRESENT'),
(8, '2026-09-18', 'BREAKFAST', 'PRESENT'),
(3, '2026-09-17', 'BREAKFAST', 'PRESENT'),
(3, '2026-09-17', 'LUNCH', 'PRESENT'),
(3, '2026-09-17', 'SNACKS', 'PRESENT'),
(3, '2026-09-17', 'DINNER', 'PRESENT'),
(4, '2026-09-17', 'BREAKFAST', 'PRESENT'),
(4, '2026-09-17', 'LUNCH', 'PRESENT'),
(4, '2026-09-17', 'DINNER', 'PRESENT'),
(5, '2026-09-17', 'BREAKFAST', 'PRESENT'),
(5, '2026-09-17', 'DINNER', 'PRESENT'),
(6, '2026-09-17', 'BREAKFAST', 'PRESENT'),
(6, '2026-09-17', 'LUNCH', 'PRESENT'),
(6, '2026-09-17', 'DINNER', 'PRESENT');

-- 6. Insert Feedbacks
INSERT INTO feedbacks (student_id, meal_type, rating, comments, feedback_date) VALUES
(3, 'LUNCH', 5, 'Paneer Butter Masala was delicious and fresh!', '2026-09-18'),
(4, 'BREAKFAST', 4, 'Poha was hot and flavourful. Good coffee.', '2026-09-18'),
(5, 'BREAKFAST', 2, 'Poha was a bit dry today, need more lemon/spices.', '2026-09-18'),
(6, 'LUNCH', 5, 'South Indian meals were authentic and satisfying.', '2026-09-18'),
(7, 'DINNER', 4, 'Good quality food and on-time service.', '2026-09-17'),
(8, 'LUNCH', 1, 'Sambar salt was too high on Wednesday.', '2026-09-16'),
(9, 'BREAKFAST', 5, 'Idlis were super soft and sambar was warm.', '2026-09-16');

-- 7. Insert Complaints
INSERT INTO complaints (student_id, category, description, status, admin_remarks, resolved_at) VALUES
(3, 'FOOD_QUALITY', 'The rice quality at dinner on Sept 16 felt undercooked.', 'RESOLVED', 'Chef was notified and grain vendor batches were checked.', '2026-09-17 14:30:00'),
(5, 'HYGIENE', 'Water dispenser near table 4 was leaking and needed cleaning.', 'RESOLVED', 'Maintenance team replaced the dispenser valve.', '2026-09-17 18:00:00'),
(7, 'TIMING', 'Snacks counter was closed 15 minutes before scheduled end time.', 'IN_PROGRESS', 'Admin issued notice to evening shift staff.', NULL),
(8, 'MENU', 'Requesting more protein-rich options (egg/paneer) in breakfast.', 'OPEN', NULL, NULL),
(10, 'STAFF', 'Staff at counter 2 was polite and helpful today.', 'OPEN', NULL, NULL);

-- 8. Insert Billing Summary Records
INSERT INTO bills (student_id, billing_month, total_meals, amount, payment_status, paid_at) VALUES
(3, 'August 2026', 92, 2760.00, 'PAID', '2026-09-02 10:15:00'),
(3, 'September 2026', 48, 1440.00, 'PENDING', NULL),
(4, 'August 2026', 88, 2640.00, 'PAID', '2026-09-03 11:20:00'),
(4, 'September 2026', 52, 1560.00, 'PENDING', NULL),
(5, 'September 2026', 42, 1260.00, 'PENDING', NULL),
(6, 'September 2026', 50, 1500.00, 'PENDING', NULL),
(7, 'September 2026', 46, 1380.00, 'PENDING', NULL),
(8, 'September 2026', 49, 1470.00, 'PENDING', NULL);

-- 9. Insert Notifications
INSERT INTO notifications (student_id, message, notification_date, is_read) VALUES
(3, 'Welcome to FoodFlow Mess Portal! Your September meal plan is active.', '2026-09-01', TRUE),
(3, 'Special Feast Menu scheduled for upcoming Friday dinner.', '2026-09-17', FALSE),
(4, 'Your complaint #1 regarding hygiene has been resolved by Admin.', '2026-09-17', TRUE),
(5, 'Mess dues for September 2026 are generated. Total: ₹1260.', '2026-09-18', FALSE),
(6, 'Reminder: Please provide feedback on today South Indian meals.', '2026-09-18', FALSE);
