package com.example.mymoney.service;

import com.example.mymoney.database.DatabaseManager;
import com.example.mymoney.model.Budget;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class BudgetService {
    private static BudgetService instance;
    private final DatabaseManager dbManager;
    private final Map<String, Budget> budgetCache;

    private BudgetService() {
        this.dbManager = DatabaseManager.getInstance();
        this.budgetCache = new HashMap<>();
    }

    public static BudgetService getInstance() {
        if (instance == null) {
            instance = new BudgetService();
        }
        return instance;
    }

    public Budget getBudget(String userId) {
        // Check cache first
        if (budgetCache.containsKey(userId)) {
            return budgetCache.get(userId);
        }

        // Load from database
        Budget budget = new Budget(userId);
        loadBudgetFromDatabase(userId, budget);
        budgetCache.put(userId, budget);
        return budget;
    }

    private void loadBudgetFromDatabase(String userId, Budget budget) {
        try {
            // Load overall budget
            String overallQuery = "SELECT amount FROM overall_budget WHERE user_id = ?";
            PreparedStatement stmt = dbManager.getConnection().prepareStatement(overallQuery);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                budget.setOverallMonthlyBudget(rs.getDouble("amount"));
            }
            stmt.close();

            // Load category budgets
            String categoryQuery = "SELECT category, amount FROM budgets WHERE user_id = ?";
            stmt = dbManager.getConnection().prepareStatement(categoryQuery);
            stmt.setString(1, userId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                budget.setCategoryBudget(rs.getString("category"), rs.getDouble("amount"));
            }
            stmt.close();

        } catch (SQLException e) {
            System.err.println("❌ Failed to load budget!");
            e.printStackTrace();
        }
    }

    public void updateOverallBudget(String userId, double amount) {
        try {
            String query = "INSERT INTO overall_budget (user_id, amount) VALUES (?, ?) ON DUPLICATE KEY UPDATE amount = ?";
            PreparedStatement stmt = dbManager.getConnection().prepareStatement(query);
            stmt.setString(1, userId);
            stmt.setDouble(2, amount);
            stmt.setDouble(3, amount);
            stmt.executeUpdate();
            stmt.close();

            // Update cache
            Budget budget = getBudget(userId);
            budget.setOverallMonthlyBudget(amount);

            System.out.println("✅ Overall budget updated: $" + amount);
        } catch (SQLException e) {
            System.err.println("❌ Failed to update overall budget!");
            e.printStackTrace();
        }
    }

    public void updateCategoryBudget(String userId, String category, double amount) {
        try {
            String query = "INSERT INTO budgets (user_id, category, amount) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE amount = ?";
            PreparedStatement stmt = dbManager.getConnection().prepareStatement(query);
            stmt.setString(1, userId);
            stmt.setString(2, category);
            stmt.setDouble(3, amount);
            stmt.setDouble(4, amount);
            stmt.executeUpdate();
            stmt.close();

            // Update cache
            Budget budget = getBudget(userId);
            budget.setCategoryBudget(category, amount);

            System.out.println("✅ Budget updated for " + category + ": $" + amount);
        } catch (SQLException e) {
            System.err.println("❌ Failed to update category budget!");
            e.printStackTrace();
        }
    }
}
