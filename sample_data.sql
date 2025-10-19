-- MyMoney Sample Data
-- Import this file AFTER creating the database schema
-- This will populate your database with test data

USE `mymoney_db`;

-- =============================================
-- Clear existing data (optional)
-- =============================================
-- Uncomment these lines if you want to start fresh
-- SET FOREIGN_KEY_CHECKS = 0;
-- TRUNCATE TABLE transactions;
-- TRUNCATE TABLE budgets;
-- TRUNCATE TABLE overall_budget;
-- TRUNCATE TABLE users;
-- SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- Sample Users
-- Password for all users: "password123"
-- =============================================

INSERT INTO `users` (`id`, `username`, `password_hash`, `salt`, `created_at`) VALUES
('user-001', 'john_doe', 'kQpGJ8LZxQqZ2mxQp7FvZA==', 'abc123salt', '2025-01-01 10:00:00'),
('user-002', 'jane_smith', 'xR9mK3PqN8vL1wQ5tY7zA==', 'xyz456salt', '2025-01-15 14:30:00'),
('user-003', 'demo', 'L5tN8wQ9vR2pM6xK4zA==', 'demo789salt', '2025-02-01 09:00:00');

-- =============================================
-- Sample Transactions for john_doe (user-001)
-- =============================================

-- January Income
INSERT INTO `transactions` (`id`, `user_id`, `type`, `amount`, `category`, `notes`, `transaction_date`, `created_at`) VALUES
('txn-001', 'user-001', 'INCOME', 5400.00, 'Salary', 'Monthly salary - January', '2025-01-05', '2025-01-05 08:00:00'),
('txn-002', 'user-001', 'INCOME', 1200.00, 'Freelance', 'Web design project', '2025-01-15', '2025-01-15 16:30:00'),
('txn-003', 'user-001', 'INCOME', 350.00, 'Investment', 'Stock dividends', '2025-01-20', '2025-01-20 10:00:00');

-- January Expenses
INSERT INTO `transactions` (`id`, `user_id`, `type`, `amount`, `category`, `notes`, `transaction_date`, `created_at`) VALUES
('txn-004', 'user-001', 'EXPENSE', 1500.00, 'Housing', 'Monthly rent payment', '2025-01-01', '2025-01-01 09:00:00'),
('txn-005', 'user-001', 'EXPENSE', 250.50, 'Groceries', 'Weekly groceries at Walmart', '2025-01-08', '2025-01-08 14:20:00'),
('txn-006', 'user-001', 'EXPENSE', 150.00, 'Utilities', 'Electric bill', '2025-01-10', '2025-01-10 11:00:00'),
('txn-007', 'user-001', 'EXPENSE', 80.00, 'Transportation', 'Gas for car', '2025-01-12', '2025-01-12 17:45:00'),
('txn-008', 'user-001', 'EXPENSE', 45.99, 'Entertainment', 'Netflix subscription', '2025-01-15', '2025-01-15 12:00:00'),
('txn-009', 'user-001', 'EXPENSE', 320.00, 'Groceries', 'Monthly Costco shopping', '2025-01-18', '2025-01-18 10:30:00'),
('txn-010', 'user-001', 'EXPENSE', 95.00, 'Healthcare', 'Pharmacy - prescriptions', '2025-01-22', '2025-01-22 15:00:00'),
('txn-011', 'user-001', 'EXPENSE', 180.00, 'Shopping', 'New work shoes', '2025-01-25', '2025-01-25 13:45:00');

-- February Income
INSERT INTO `transactions` (`id`, `user_id`, `type`, `amount`, `category`, `notes`, `transaction_date`, `created_at`) VALUES
('txn-012', 'user-001', 'INCOME', 5400.00, 'Salary', 'Monthly salary - February', '2025-02-05', '2025-02-05 08:00:00'),
('txn-013', 'user-001', 'INCOME', 800.00, 'Freelance', 'Logo design project', '2025-02-12', '2025-02-12 14:00:00');

-- February Expenses
INSERT INTO `transactions` (`id`, `user_id`, `type`, `amount`, `category`, `notes`, `transaction_date`, `created_at`) VALUES
('txn-014', 'user-001', 'EXPENSE', 1500.00, 'Housing', 'Monthly rent payment', '2025-02-01', '2025-02-01 09:00:00'),
('txn-015', 'user-001', 'EXPENSE', 280.00, 'Groceries', 'Weekly shopping', '2025-02-05', '2025-02-05 16:00:00'),
('txn-016', 'user-001', 'EXPENSE', 65.00, 'Transportation', 'Gas for car', '2025-02-10', '2025-02-10 18:30:00'),
('txn-017', 'user-001', 'EXPENSE', 120.00, 'Entertainment', 'Concert tickets', '2025-02-14', '2025-02-14 20:00:00');

-- March Income (Current Month)
INSERT INTO `transactions` (`id`, `user_id`, `type`, `amount`, `category`, `notes`, `transaction_date`, `created_at`) VALUES
('txn-018', 'user-001', 'INCOME', 5400.00, 'Salary', 'Monthly salary - March', '2025-03-05', '2025-03-05 08:00:00'),
('txn-019', 'user-001', 'INCOME', 600.00, 'Freelance', 'UI/UX consulting', '2025-03-10', '2025-03-10 11:00:00');

-- March Expenses (Current Month)
INSERT INTO `transactions` (`id`, `user_id`, `type`, `amount`, `category`, `notes`, `transaction_date`, `created_at`) VALUES
('txn-020', 'user-001', 'EXPENSE', 1500.00, 'Housing', 'Monthly rent payment', '2025-03-01', '2025-03-01 09:00:00'),
('txn-021', 'user-001', 'EXPENSE', 145.00, 'Groceries', 'Grocery shopping', '2025-03-03', '2025-03-03 15:00:00'),
('txn-022', 'user-001', 'EXPENSE', 200.00, 'Utilities', 'Electric + Water bills', '2025-03-08', '2025-03-08 10:00:00'),
('txn-023', 'user-001', 'EXPENSE', 75.00, 'Transportation', 'Gas for car', '2025-03-12', '2025-03-12 17:00:00'),
('txn-024', 'user-001', 'EXPENSE', 89.99, 'Shopping', 'Online shopping - Amazon', '2025-03-15', '2025-03-15 14:30:00');

-- =============================================
-- Sample Transactions for jane_smith (user-002)
-- =============================================

INSERT INTO `transactions` (`id`, `user_id`, `type`, `amount`, `category`, `notes`, `transaction_date`, `created_at`) VALUES
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

INSERT INTO `transactions` (`id`, `user_id`, `type`, `amount`, `category`, `notes`, `transaction_date`, `created_at`) VALUES
('txn-201', 'user-003', 'INCOME', 4500.00, 'Salary', 'Monthly income', '2025-03-05', '2025-03-05 08:00:00'),
('txn-202', 'user-003', 'EXPENSE', 1200.00, 'Housing', 'Rent', '2025-03-01', '2025-03-01 09:00:00'),
('txn-203', 'user-003', 'EXPENSE', 200.00, 'Groceries', 'Grocery shopping', '2025-03-05', '2025-03-05 14:00:00'),
('txn-204', 'user-003', 'EXPENSE', 150.00, 'Utilities', 'Bills', '2025-03-10', '2025-03-10 11:00:00'),
('txn-205', 'user-003', 'EXPENSE', 60.00, 'Transportation', 'Gas', '2025-03-12', '2025-03-12 16:00:00'),
('txn-206', 'user-003', 'EXPENSE', 80.00, 'Entertainment', 'Movies + Dinner', '2025-03-15', '2025-03-15 20:00:00');

-- =============================================
-- Sample Overall Budgets
-- =============================================

INSERT INTO `overall_budget` (`user_id`, `amount`, `created_at`, `updated_at`) VALUES
('user-001', 3500.00, '2025-01-01 10:00:00', '2025-01-01 10:00:00'),
('user-002', 4000.00, '2025-01-15 14:30:00', '2025-01-15 14:30:00'),
('user-003', 3000.00, '2025-02-01 09:00:00', '2025-02-01 09:00:00');

-- =============================================
-- Sample Category Budgets
-- =============================================

-- Budgets for john_doe (user-001)
INSERT INTO `budgets` (`user_id`, `category`, `amount`, `created_at`, `updated_at`) VALUES
('user-001', 'Groceries', 600.00, '2025-01-01 10:00:00', '2025-01-01 10:00:00'),
('user-001', 'Utilities', 200.00, '2025-01-01 10:00:00', '2025-01-01 10:00:00'),
('user-001', 'Transportation', 300.00, '2025-01-01 10:00:00', '2025-01-01 10:00:00'),
('user-001', 'Entertainment', 250.00, '2025-01-01 10:00:00', '2025-01-01 10:00:00'),
('user-001', 'Healthcare', 150.00, '2025-01-01 10:00:00', '2025-01-01 10:00:00'),
('user-001', 'Shopping', 400.00, '2025-01-01 10:00:00', '2025-01-01 10:00:00');

-- Budgets for jane_smith (user-002)
INSERT INTO `budgets` (`user_id`, `category`, `amount`, `created_at`, `updated_at`) VALUES
('user-002', 'Groceries', 800.00, '2025-01-15 14:30:00', '2025-01-15 14:30:00'),
('user-002', 'Utilities', 250.00, '2025-01-15 14:30:00', '2025-01-15 14:30:00'),
('user-002', 'Transportation', 350.00, '2025-01-15 14:30:00', '2025-01-15 14:30:00'),
('user-002', 'Entertainment', 500.00, '2025-01-15 14:30:00', '2025-01-15 14:30:00'),
('user-002', 'Healthcare', 200.00, '2025-01-15 14:30:00', '2025-01-15 14:30:00'),
('user-002', 'Shopping', 600.00, '2025-01-15 14:30:00', '2025-01-15 14:30:00');

-- Budgets for demo (user-003)
INSERT INTO `budgets` (`user_id`, `category`, `amount`, `created_at`, `updated_at`) VALUES
('user-003', 'Groceries', 500.00, '2025-02-01 09:00:00', '2025-02-01 09:00:00'),
('user-003', 'Utilities', 180.00, '2025-02-01 09:00:00', '2025-02-01 09:00:00'),
('user-003', 'Transportation', 250.00, '2025-02-01 09:00:00', '2025-02-01 09:00:00'),
('user-003', 'Entertainment', 200.00, '2025-02-01 09:00:00', '2025-02-01 09:00:00'),
('user-003', 'Healthcare', 120.00, '2025-02-01 09:00:00', '2025-02-01 09:00:00'),
('user-003', 'Shopping', 300.00, '2025-02-01 09:00:00', '2025-02-01 09:00:00');

-- =============================================
-- Summary Report
-- =============================================

SELECT 'Sample data imported successfully!' AS Status;

SELECT 
    'Users Created' AS Item,
    COUNT(*) AS Count
FROM users
UNION ALL
SELECT 
    'Transactions Created' AS Item,
    COUNT(*) AS Count
FROM transactions
UNION ALL
SELECT 
    'Budgets Set' AS Item,
    COUNT(*) AS Count
FROM budgets;

SELECT 
    username,
    COUNT(t.id) AS total_transactions,
    SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) AS total_income,
    SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) AS total_expenses,
    SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) - 
    SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) AS balance
FROM users u
LEFT JOIN transactions t ON u.id = t.user_id
GROUP BY u.id, u.username;

