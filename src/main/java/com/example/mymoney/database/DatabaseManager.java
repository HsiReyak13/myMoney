package com.example.mymoney.database;

import java.sql.*;

public class DatabaseManager {
    private static DatabaseManager instance;
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mymoney_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";  // Empty password for XAMPP default
    
    private Connection connection;

    private DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            createTables();
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to database!");
            System.err.println("Make sure XAMPP MySQL is running on port 3306");
            e.printStackTrace();
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void createTables() {
        try {
            Statement stmt = connection.createStatement();

            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id VARCHAR(36) PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password_hash VARCHAR(255) NOT NULL,
                    salt VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            stmt.execute(createUsersTable);

            String createTransactionsTable = """
                CREATE TABLE IF NOT EXISTS transactions (
                    id VARCHAR(36) PRIMARY KEY,
                    user_id VARCHAR(36) NOT NULL,
                    type ENUM('INCOME', 'EXPENSE') NOT NULL,
                    amount DECIMAL(10, 2) NOT NULL,
                    category VARCHAR(50) NOT NULL,
                    notes TEXT,
                    transaction_date DATE NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )
            """;
            stmt.execute(createTransactionsTable);

            String createBudgetsTable = """
                CREATE TABLE IF NOT EXISTS budgets (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id VARCHAR(36) NOT NULL,
                    category VARCHAR(50) NOT NULL,
                    amount DECIMAL(10, 2) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    UNIQUE KEY unique_user_category (user_id, category),
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )
            """;
            stmt.execute(createBudgetsTable);

            String createOverallBudgetTable = """
                CREATE TABLE IF NOT EXISTS overall_budget (
                    user_id VARCHAR(36) PRIMARY KEY,
                    amount DECIMAL(10, 2) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )
            """;
            stmt.execute(createOverallBudgetTable);

            
            stmt.close();
        } catch (SQLException e) {
            System.err.println("❌ Error creating tables!");
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

