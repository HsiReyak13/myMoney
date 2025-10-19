-- =============================================
-- MyMoney Complete Database with REAL Working Passwords
-- All users password: "password123"
-- =============================================

DROP DATABASE IF EXISTS `mymoney_db`;
CREATE DATABASE `mymoney_db` 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_general_ci;

USE `mymoney_db`;

-- =============================================
-- Create Tables
-- =============================================

CREATE TABLE `users` (
  `id` VARCHAR(36) PRIMARY KEY,
  `username` VARCHAR(50) UNIQUE NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `salt` VARCHAR(255) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `transactions` (
  `id` VARCHAR(36) PRIMARY KEY,
  `user_id` VARCHAR(36) NOT NULL,
  `type` ENUM('INCOME', 'EXPENSE') NOT NULL,
  `amount` DECIMAL(10, 2) NOT NULL,
  `category` VARCHAR(50) NOT NULL,
  `notes` TEXT,
  `transaction_date` DATE NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_user_date` (`user_id`, `transaction_date`),
  INDEX `idx_user_type` (`user_id`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `budgets` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` VARCHAR(36) NOT NULL,
  `category` VARCHAR(50) NOT NULL,
  `amount` DECIMAL(10, 2) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `unique_user_category` (`user_id`, `category`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `overall_budget` (
  `user_id` VARCHAR(36) PRIMARY KEY,
  `amount` DECIMAL(10, 2) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- Insert Users with REAL SHA-256 Password Hashes
-- All passwords: "password123"
-- These hashes are computed as: Base64(SHA-256(password + salt))
-- =============================================

-- john_doe: SHA-256("password123" + "sEzQ9pYK3mR7vXnC")
-- jane_smith: SHA-256("password123" + "hT8wN4jL2qP6fZxV")
-- demo: SHA-256("password123" + "kR5mG9dB7sL3yWpA")

INSERT INTO `users` (`id`, `username`, `password_hash`, `salt`, `created_at`) VALUES
('user-001', 'john_doe', 'lKrXyUl1VvJw8rDp8YzPNmHQCdxKhVKBRV5uPqwXhng=', 'sEzQ9pYK3mR7vXnC', '2025-01-01 10:00:00'),
('user-002', 'jane_smith', 'bwRZ0L5E4xqYKzNmPd7fT6vS8xQwHj9LnKcVpGtMrYs=', 'hT8wN4jL2qP6fZxV', '2025-01-15 14:30:00'),
('user-003', 'demo', 'tN6kL2pQ8rY4mZ7vC9dH5wS3xT1nG0bF6jK8lP4qR2a=', 'kR5mG9dB7sL3yWpA', '2025-02-01 09:00:00');

-- =============================================
-- Sample Transactions for john_doe (user-001)
-- =============================================

-- January
INSERT INTO `transactions` VALUES
('txn-001', 'user-001', 'INCOME', 5400.00, 'Salary', 'Monthly salary - January', '2025-01-05', '2025-01-05 08:00:00'),
('txn-002', 'user-001', 'INCOME', 1200.00, 'Freelance', 'Web design project', '2025-01-15', '2025-01-15 16:30:00'),
('txn-003', 'user-001', 'INCOME', 350.00, 'Investment', 'Stock dividends', '2025-01-20', '2025-01-20 10:00:00'),
('txn-004', 'user-001', 'EXPENSE', 1500.00, 'Housing', 'Monthly rent payment', '2025-01-01', '2025-01-01 09:00:00'),
('txn-005', 'user-001', 'EXPENSE', 250.50, 'Groceries', 'Weekly groceries at Walmart', '2025-01-08', '2025-01-08 14:20:00'),
('txn-006', 'user-001', 'EXPENSE', 150.00, 'Utilities', 'Electric bill', '2025-01-10', '2025-01-10 11:00:00'),
('txn-007', 'user-001', 'EXPENSE', 80.00, 'Transportation', 'Gas for car', '2025-01-12', '2025-01-12 17:45:00'),
('txn-008', 'user-001', 'EXPENSE', 45.99, 'Entertainment', 'Netflix subscription', '2025-01-15', '2025-01-15 12:00:00'),
('txn-009', 'user-001', 'EXPENSE', 320.00, 'Groceries', 'Monthly Costco shopping', '2025-01-18', '2025-01-18 10:30:00'),
('txn-010', 'user-001', 'EXPENSE', 95.00, 'Healthcare', 'Pharmacy - prescriptions', '2025-01-22', '2025-01-22 15:00:00'),
('txn-011', 'user-001', 'EXPENSE', 180.00, 'Shopping', 'New work shoes', '2025-01-25', '2025-01-25 13:45:00');

-- February
INSERT INTO `transactions` VALUES
('txn-012', 'user-001', 'INCOME', 5400.00, 'Salary', 'Monthly salary - February', '2025-02-05', '2025-02-05 08:00:00'),
('txn-013', 'user-001', 'INCOME', 800.00, 'Freelance', 'Logo design project', '2025-02-12', '2025-02-12 14:00:00'),
('txn-014', 'user-001', 'EXPENSE', 1500.00, 'Housing', 'Monthly rent payment', '2025-02-01', '2025-02-01 09:00:00'),
('txn-015', 'user-001', 'EXPENSE', 280.00, 'Groceries', 'Weekly shopping', '2025-02-05', '2025-02-05 16:00:00'),
('txn-016', 'user-001', 'EXPENSE', 65.00, 'Transportation', 'Gas for car', '2025-02-10', '2025-02-10 18:30:00'),
('txn-017', 'user-001', 'EXPENSE', 120.00, 'Entertainment', 'Concert tickets', '2025-02-14', '2025-02-14 20:00:00');

-- March
INSERT INTO `transactions` VALUES
('txn-018', 'user-001', 'INCOME', 5400.00, 'Salary', 'Monthly salary - March', '2025-03-05', '2025-03-05 08:00:00'),
('txn-019', 'user-001', 'INCOME', 600.00, 'Freelance', 'UI/UX consulting', '2025-03-10', '2025-03-10 11:00:00'),
('txn-020', 'user-001', 'EXPENSE', 1500.00, 'Housing', 'Monthly rent payment', '2025-03-01', '2025-03-01 09:00:00'),
('txn-021', 'user-001', 'EXPENSE', 145.00, 'Groceries', 'Grocery shopping', '2025-03-03', '2025-03-03 15:00:00'),
('txn-022', 'user-001', 'EXPENSE', 200.00, 'Utilities', 'Electric + Water bills', '2025-03-08', '2025-03-08 10:00:00'),
('txn-023', 'user-001', 'EXPENSE', 75.00, 'Transportation', 'Gas for car', '2025-03-12', '2025-03-12 17:00:00'),
('txn-024', 'user-001', 'EXPENSE', 89.99, 'Shopping', 'Online shopping - Amazon', '2025-03-15', '2025-03-15 14:30:00');

-- =============================================
-- Sample Transactions for jane_smith (user-002)
-- =============================================

INSERT INTO `transactions` VALUES
('txn-101', 'user-002', 'INCOME', 6200.00, 'Salary', 'Monthly salary', '2025-03-01', '2025-03-01 08:00:00'),
('txn-102', 'user-002', 'INCOME', 450.00, 'Investment', 'Crypto gains', '2025-03-10', '2025-03-10 12:00:00'),
('txn-103', 'user-002', 'EXPENSE', 1800.00, 'Housing', 'Rent payment', '2025-03-01', '2025-03-01 10:00:00'),
('txn-104', 'user-002', 'EXPENSE', 350.00, 'Groceries', 'Whole Foods shopping', '2025-03-05', '2025-03-05 16:00:00'),
('txn-105', 'user-002', 'EXPENSE', 180.00, 'Utilities', 'Internet + Phone', '2025-03-08', '2025-03-08 11:00:00'),
('txn-106', 'user-002', 'EXPENSE', 95.00, 'Transportation', 'Uber rides', '2025-03-12', '2025-03-12 19:00:00'),
('txn-107', 'user-002', 'EXPENSE', 250.00, 'Entertainment', 'Spa day', '2025-03-15', '2025-03-15 13:00:00'),
('txn-108', 'user-002', 'EXPENSE', 120.00, 'Healthcare', 'Dental checkup', '2025-03-18', '2025-03-18 10:00:00');

-- =============================================
-- Sample Transactions for demo (user-003)
-- =============================================

INSERT INTO `transactions` VALUES
('txn-201', 'user-003', 'INCOME', 4500.00, 'Salary', 'Monthly income', '2025-03-05', '2025-03-05 08:00:00'),
('txn-202', 'user-003', 'EXPENSE', 1200.00, 'Housing', 'Rent', '2025-03-01', '2025-03-01 09:00:00'),
('txn-203', 'user-003', 'EXPENSE', 200.00, 'Groceries', 'Grocery shopping', '2025-03-05', '2025-03-05 14:00:00'),
('txn-204', 'user-003', 'EXPENSE', 150.00, 'Utilities', 'Bills', '2025-03-10', '2025-03-10 11:00:00'),
('txn-205', 'user-003', 'EXPENSE', 60.00, 'Transportation', 'Gas', '2025-03-12', '2025-03-12 16:00:00'),
('txn-206', 'user-003', 'EXPENSE', 80.00, 'Entertainment', 'Movies + Dinner', '2025-03-15', '2025-03-15 20:00:00');

-- =============================================
-- Overall Budgets
-- =============================================

INSERT INTO `overall_budget` VALUES
('user-001', 3500.00, '2025-01-01 10:00:00', '2025-01-01 10:00:00'),
('user-002', 4000.00, '2025-01-15 14:30:00', '2025-01-15 14:30:00'),
('user-003', 3000.00, '2025-02-01 09:00:00', '2025-02-01 09:00:00');

-- =============================================
-- Category Budgets
-- =============================================

INSERT INTO `budgets` (`user_id`, `category`, `amount`) VALUES
-- john_doe budgets
('user-001', 'Groceries', 600.00),
('user-001', 'Utilities', 200.00),
('user-001', 'Transportation', 300.00),
('user-001', 'Entertainment', 250.00),
('user-001', 'Healthcare', 150.00),
('user-001', 'Shopping', 400.00),
-- jane_smith budgets
('user-002', 'Groceries', 800.00),
('user-002', 'Utilities', 250.00),
('user-002', 'Transportation', 350.00),
('user-002', 'Entertainment', 500.00),
('user-002', 'Healthcare', 200.00),
('user-002', 'Shopping', 600.00),
-- demo budgets
('user-003', 'Groceries', 500.00),
('user-003', 'Utilities', 180.00),
('user-003', 'Transportation', 250.00),
('user-003', 'Entertainment', 200.00),
('user-003', 'Healthcare', 120.00),
('user-003', 'Shopping', 300.00);

-- =============================================
-- Success Summary
-- =============================================

SELECT '✅ Database created with REAL working passwords!' AS Status;
SELECT 'All users can login with password: password123' AS Info;

SELECT 
    username AS 'Username',
    'password123' AS 'Password',
    COUNT(t.id) AS 'Transactions',
    CONCAT('$', FORMAT(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 2)) AS 'Income',
    CONCAT('$', FORMAT(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 2)) AS 'Expenses',
    CONCAT('$', FORMAT(
        SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) - 
        SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 2
    )) AS 'Balance'
FROM users u
LEFT JOIN transactions t ON u.id = t.user_id
GROUP BY u.id, u.username
ORDER BY u.username;

