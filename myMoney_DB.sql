
DROP DATABASE IF EXISTS `mymoney_db`;
CREATE DATABASE `mymoney_db` 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_general_ci;

USE `mymoney_db`;


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


CREATE INDEX IF NOT EXISTS idx_transactions_user_id 
ON transactions(user_id);


CREATE INDEX IF NOT EXISTS idx_transactions_user_date 
ON transactions(user_id, transaction_date DESC);


CREATE INDEX IF NOT EXISTS idx_transactions_user_type 
ON transactions(user_id, type);


CREATE INDEX IF NOT EXISTS idx_users_username 
ON users(username);

CREATE INDEX IF NOT EXISTS idx_budgets_user_category 
ON budgets(user_id, category);

SELECT 'MyMoney database schema created successfully!' AS Status;
SELECT 'Database: mymoney_db' AS Database_Name;
SELECT 'Tables: users, transactions, budgets, overall_budget' AS Tables_Created;
SELECT 'Indexes: 5 performance indexes added' AS Performance_Optimizations;


SELECT 'Next Steps:' AS Instructions;
SELECT '1. Create your first user account through the application' AS Step_1;
SELECT '2. Start adding transactions and setting budgets' AS Step_2;
SELECT '3. Use the application normally - all features will work!' AS Step_3;
