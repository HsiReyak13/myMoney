package com.example.mymoney.model;

import java.util.HashMap;
import java.util.Map;

public class Budget {
    private final String userId;
    private double overallMonthlyBudget;
    private final Map<String, Double> categoryBudgets;

    public Budget(String userId) {
        this.userId = userId;
        this.overallMonthlyBudget = 3500.0; // Default budget
        this.categoryBudgets = new HashMap<>();
        initializeDefaultBudgets();
    }

    private void initializeDefaultBudgets() {
        categoryBudgets.put("Groceries", 600.0);
        categoryBudgets.put("Utilities", 200.0);
        categoryBudgets.put("Transportation", 300.0);
        categoryBudgets.put("Entertainment", 250.0);
        categoryBudgets.put("Healthcare", 150.0);
        categoryBudgets.put("Shopping", 400.0);
    }

    public String getUserId() {
        return userId;
    }

    public double getOverallMonthlyBudget() {
        return overallMonthlyBudget;
    }

    public void setOverallMonthlyBudget(double overallMonthlyBudget) {
        this.overallMonthlyBudget = overallMonthlyBudget;
    }

    public Map<String, Double> getCategoryBudgets() {
        return categoryBudgets;
    }

    public void setCategoryBudget(String category, double amount) {
        categoryBudgets.put(category, amount);
    }

    public double getCategoryBudget(String category) {
        return categoryBudgets.getOrDefault(category, 0.0);
    }
}

