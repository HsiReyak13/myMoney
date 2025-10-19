-- =============================================
-- MyMoney - Data Only (No Users)
-- Register users through the app, then run this
-- =============================================

USE `mymoney_db`;

-- Clear existing data
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE transactions;
TRUNCATE TABLE budgets;
TRUNCATE TABLE overall_budget;
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- INSTRUCTIONS:
-- 1. Register these users in the app first:
--    - john_doe / password123
--    - jane_smith / password123  
--    - demo / password123
-- 2. Then run this SQL to add their data
-- =============================================

-- Get user IDs (you'll need to update these after registration)
SET @john_id = (SELECT id FROM users WHERE username = 'john_doe');
SET @jane_id = (SELECT id FROM users WHERE username = 'jane_smith');
SET @demo_id = (SELECT id FROM users WHERE username = 'demo');

-- =============================================
-- Transactions for john_doe
-- =============================================

INSERT INTO `transactions` (`id`, `user_id`, `type`, `amount`, `category`, `notes`, `transaction_date`, `created_at`) 
SELECT 'txn-001', @john_id, 'INCOME', 5400.00, 'Salary', 'Monthly salary - January', '2025-01-05', '2025-01-05 08:00:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-002', @john_id, 'INCOME', 1200.00, 'Freelance', 'Web design project', '2025-01-15', '2025-01-15 16:30:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-003', @john_id, 'INCOME', 350.00, 'Investment', 'Stock dividends', '2025-01-20', '2025-01-20 10:00:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-004', @john_id, 'EXPENSE', 1500.00, 'Housing', 'Monthly rent payment', '2025-01-01', '2025-01-01 09:00:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-005', @john_id, 'EXPENSE', 250.50, 'Groceries', 'Weekly groceries at Walmart', '2025-01-08', '2025-01-08 14:20:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-006', @john_id, 'EXPENSE', 150.00, 'Utilities', 'Electric bill', '2025-01-10', '2025-01-10 11:00:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-007', @john_id, 'EXPENSE', 80.00, 'Transportation', 'Gas for car', '2025-01-12', '2025-01-12 17:45:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-008', @john_id, 'EXPENSE', 45.99, 'Entertainment', 'Netflix subscription', '2025-01-15', '2025-01-15 12:00:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-009', @john_id, 'EXPENSE', 320.00, 'Groceries', 'Monthly Costco shopping', '2025-01-18', '2025-01-18 10:30:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-010', @john_id, 'EXPENSE', 95.00, 'Healthcare', 'Pharmacy - prescriptions', '2025-01-22', '2025-01-22 15:00:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-011', @john_id, 'EXPENSE', 180.00, 'Shopping', 'New work shoes', '2025-01-25', '2025-01-25 13:45:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-012', @john_id, 'INCOME', 5400.00, 'Salary', 'Monthly salary - February', '2025-02-05', '2025-02-05 08:00:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-013', @john_id, 'INCOME', 800.00, 'Freelance', 'Logo design project', '2025-02-12', '2025-02-12 14:00:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-014', @john_id, 'EXPENSE', 1500.00, 'Housing', 'Monthly rent payment', '2025-02-01', '2025-02-01 09:00:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-015', @john_id, 'EXPENSE', 280.00, 'Groceries', 'Weekly shopping', '2025-02-05', '2025-02-05 16:00:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-016', @john_id, 'EXPENSE', 65.00, 'Transportation', 'Gas for car', '2025-02-10', '2025-02-10 18:30:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-017', @john_id, 'EXPENSE', 120.00, 'Entertainment', 'Concert tickets', '2025-02-14', '2025-02-14 20:00:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-018', @john_id, 'INCOME', 5400.00, 'Salary', 'Monthly salary - March', '2025-03-05', '2025-03-05 08:00:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-019', @john_id, 'INCOME', 600.00, 'Freelance', 'UI/UX consulting', '2025-03-10', '2025-03-10 11:00:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-020', @john_id, 'EXPENSE', 1500.00, 'Housing', 'Monthly rent payment', '2025-03-01', '2025-03-01 09:00:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-021', @john_id, 'EXPENSE', 145.00, 'Groceries', 'Grocery shopping', '2025-03-03', '2025-03-03 15:00:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-022', @john_id, 'EXPENSE', 200.00, 'Utilities', 'Electric + Water bills', '2025-03-08', '2025-03-08 10:00:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-023', @john_id, 'EXPENSE', 75.00, 'Transportation', 'Gas for car', '2025-03-12', '2025-03-12 17:00:00' WHERE @john_id IS NOT NULL
UNION ALL SELECT 'txn-024', @john_id, 'EXPENSE', 89.99, 'Shopping', 'Online shopping - Amazon', '2025-03-15', '2025-03-15 14:30:00' WHERE @john_id IS NOT NULL;

-- =============================================
-- Transactions for jane_smith
-- =============================================

INSERT INTO `transactions` (`id`, `user_id`, `type`, `amount`, `category`, `notes`, `transaction_date`, `created_at`)
SELECT 'txn-101', @jane_id, 'INCOME', 6200.00, 'Salary', 'Monthly salary', '2025-03-01', '2025-03-01 08:00:00' WHERE @jane_id IS NOT NULL
UNION ALL SELECT 'txn-102', @jane_id, 'INCOME', 450.00, 'Investment', 'Crypto gains', '2025-03-10', '2025-03-10 12:00:00' WHERE @jane_id IS NOT NULL
UNION ALL SELECT 'txn-103', @jane_id, 'EXPENSE', 1800.00, 'Housing', 'Rent payment', '2025-03-01', '2025-03-01 10:00:00' WHERE @jane_id IS NOT NULL
UNION ALL SELECT 'txn-104', @jane_id, 'EXPENSE', 350.00, 'Groceries', 'Whole Foods shopping', '2025-03-05', '2025-03-05 16:00:00' WHERE @jane_id IS NOT NULL
UNION ALL SELECT 'txn-105', @jane_id, 'EXPENSE', 180.00, 'Utilities', 'Internet + Phone', '2025-03-08', '2025-03-08 11:00:00' WHERE @jane_id IS NOT NULL
UNION ALL SELECT 'txn-106', @jane_id, 'EXPENSE', 95.00, 'Transportation', 'Uber rides', '2025-03-12', '2025-03-12 19:00:00' WHERE @jane_id IS NOT NULL
UNION ALL SELECT 'txn-107', @jane_id, 'EXPENSE', 250.00, 'Entertainment', 'Spa day', '2025-03-15', '2025-03-15 13:00:00' WHERE @jane_id IS NOT NULL
UNION ALL SELECT 'txn-108', @jane_id, 'EXPENSE', 120.00, 'Healthcare', 'Dental checkup', '2025-03-18', '2025-03-18 10:00:00' WHERE @jane_id IS NOT NULL;

-- =============================================
-- Transactions for demo
-- =============================================

INSERT INTO `transactions` (`id`, `user_id`, `type`, `amount`, `category`, `notes`, `transaction_date`, `created_at`)
SELECT 'txn-201', @demo_id, 'INCOME', 4500.00, 'Salary', 'Monthly income', '2025-03-05', '2025-03-05 08:00:00' WHERE @demo_id IS NOT NULL
UNION ALL SELECT 'txn-202', @demo_id, 'EXPENSE', 1200.00, 'Housing', 'Rent', '2025-03-01', '2025-03-01 09:00:00' WHERE @demo_id IS NOT NULL
UNION ALL SELECT 'txn-203', @demo_id, 'EXPENSE', 200.00, 'Groceries', 'Grocery shopping', '2025-03-05', '2025-03-05 14:00:00' WHERE @demo_id IS NOT NULL
UNION ALL SELECT 'txn-204', @demo_id, 'EXPENSE', 150.00, 'Utilities', 'Bills', '2025-03-10', '2025-03-10 11:00:00' WHERE @demo_id IS NOT NULL
UNION ALL SELECT 'txn-205', @demo_id, 'EXPENSE', 60.00, 'Transportation', 'Gas', '2025-03-12', '2025-03-12 16:00:00' WHERE @demo_id IS NOT NULL
UNION ALL SELECT 'txn-206', @demo_id, 'EXPENSE', 80.00, 'Entertainment', 'Movies + Dinner', '2025-03-15', '2025-03-15 20:00:00' WHERE @demo_id IS NOT NULL;

-- =============================================
-- Budgets
-- =============================================

INSERT INTO `overall_budget` (`user_id`, `amount`) 
SELECT @john_id, 3500.00 WHERE @john_id IS NOT NULL
UNION ALL SELECT @jane_id, 4000.00 WHERE @jane_id IS NOT NULL
UNION ALL SELECT @demo_id, 3000.00 WHERE @demo_id IS NOT NULL;

INSERT INTO `budgets` (`user_id`, `category`, `amount`)
SELECT @john_id, 'Groceries', 600.00 WHERE @john_id IS NOT NULL
UNION ALL SELECT @john_id, 'Utilities', 200.00 WHERE @john_id IS NOT NULL
UNION ALL SELECT @john_id, 'Transportation', 300.00 WHERE @john_id IS NOT NULL
UNION ALL SELECT @john_id, 'Entertainment', 250.00 WHERE @john_id IS NOT NULL
UNION ALL SELECT @john_id, 'Healthcare', 150.00 WHERE @john_id IS NOT NULL
UNION ALL SELECT @john_id, 'Shopping', 400.00 WHERE @john_id IS NOT NULL
UNION ALL SELECT @jane_id, 'Groceries', 800.00 WHERE @jane_id IS NOT NULL
UNION ALL SELECT @jane_id, 'Utilities', 250.00 WHERE @jane_id IS NOT NULL
UNION ALL SELECT @jane_id, 'Transportation', 350.00 WHERE @jane_id IS NOT NULL
UNION ALL SELECT @jane_id, 'Entertainment', 500.00 WHERE @jane_id IS NOT NULL
UNION ALL SELECT @jane_id, 'Healthcare', 200.00 WHERE @jane_id IS NOT NULL
UNION ALL SELECT @jane_id, 'Shopping', 600.00 WHERE @jane_id IS NOT NULL
UNION ALL SELECT @demo_id, 'Groceries', 500.00 WHERE @demo_id IS NOT NULL
UNION ALL SELECT @demo_id, 'Utilities', 180.00 WHERE @demo_id IS NOT NULL
UNION ALL SELECT @demo_id, 'Transportation', 250.00 WHERE @demo_id IS NOT NULL
UNION ALL SELECT @demo_id, 'Entertainment', 200.00 WHERE @demo_id IS NOT NULL
UNION ALL SELECT @demo_id, 'Healthcare', 120.00 WHERE @demo_id IS NOT NULL
UNION ALL SELECT @demo_id, 'Shopping', 300.00 WHERE @demo_id IS NOT NULL;

SELECT '✅ Sample data loaded successfully!' AS Status;
SELECT COUNT(*) AS 'Transactions Added' FROM transactions;

