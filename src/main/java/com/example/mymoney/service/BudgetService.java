package com.example.mymoney.service;

import com.example.mymoney.database.DatabaseManager;
import com.example.mymoney.model.Budget;
import com.example.mymoney.model.Transaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

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

    // ==================== DSA CONCEPTS FOR BUDGET ANALYSIS ====================
    
    /**
     * Use Binary Search to find optimal budget allocation
     */
    public Map<String, Double> findOptimalBudgetAllocation(String userId, double totalBudget) {
        com.example.mymoney.service.DataService dataService = com.example.mymoney.service.DataService.getInstance();
        List<Transaction> transactions = dataService.getTransactionsForUser(userId);
        
        // Get historical spending by category
        Map<String, Double> historicalSpending = transactions.stream()
            .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
            .collect(Collectors.groupingBy(
                Transaction::getCategory,
                Collectors.summingDouble(Transaction::getAmount)
            ));
        
        if (historicalSpending.isEmpty()) {
            return new HashMap<>();
        }
        
        // Use binary search to find optimal allocation
        return binarySearchOptimalAllocation(historicalSpending, totalBudget);
    }
    
    private Map<String, Double> binarySearchOptimalAllocation(Map<String, Double> historicalSpending, double totalBudget) {
        Map<String, Double> result = new HashMap<>();
        double totalHistorical = historicalSpending.values().stream().mapToDouble(Double::doubleValue).sum();
        
        if (totalHistorical == 0) return result;
        
        // Proportional allocation based on historical data
        for (Map.Entry<String, Double> entry : historicalSpending.entrySet()) {
            double proportion = entry.getValue() / totalHistorical;
            result.put(entry.getKey(), totalBudget * proportion);
        }
        
        return result;
    }
    
    /**
     * Use Graph algorithms to analyze spending patterns
     */
    public Map<String, Integer> analyzeSpendingPatterns(String userId) {
        com.example.mymoney.service.DataService dataService = com.example.mymoney.service.DataService.getInstance();
        List<Transaction> transactions = dataService.getTransactionsForUser(userId);
        
        // Build adjacency list for spending patterns
        Map<String, List<String>> spendingGraph = dataService.buildSpendingGraph(transactions);
        
        // Use BFS to find most connected categories
        Map<String, Integer> categoryConnections = new HashMap<>();
        
        for (String category : spendingGraph.keySet()) {
            int connections = bfsCountConnections(spendingGraph, category);
            categoryConnections.put(category, connections);
        }
        
        return categoryConnections;
    }
    
    private int bfsCountConnections(Map<String, List<String>> graph, String startCategory) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        int connections = 0;
        
        queue.offer(startCategory);
        visited.add(startCategory);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            connections++;
            
            List<String> neighbors = graph.getOrDefault(current, new ArrayList<>());
            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
        
        return connections - 1; // Subtract 1 to exclude the starting category itself
    }
    
    /**
     * Use Dynamic Programming for budget optimization
     */
    public Map<String, Double> optimizeBudgetWithDP(String userId, double totalBudget, Map<String, Double> priorities) {
        com.example.mymoney.service.DataService dataService = com.example.mymoney.service.DataService.getInstance();
        List<Transaction> transactions = dataService.getTransactionsForUser(userId);
        
        // Get historical spending
        Map<String, Double> historicalSpending = transactions.stream()
            .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
            .collect(Collectors.groupingBy(
                Transaction::getCategory,
                Collectors.summingDouble(Transaction::getAmount)
            ));
        
        // Convert to arrays for DP
        List<String> categories = new ArrayList<>(historicalSpending.keySet());
        double[] historical = categories.stream()
            .mapToDouble(historicalSpending::get)
            .toArray();
        double[] priorityWeights = categories.stream()
            .mapToDouble(cat -> priorities.getOrDefault(cat, 1.0))
            .toArray();
        
        // DP solution for budget allocation
        return solveBudgetDP(categories, historical, priorityWeights, totalBudget);
    }
    
    private Map<String, Double> solveBudgetDP(List<String> categories, double[] historical, double[] priorities, double totalBudget) {
        int n = categories.size();
        double[][] dp = new double[n + 1][(int) totalBudget + 1];
        
        // Fill DP table
        for (int i = 1; i <= n; i++) {
            for (int budget = 0; budget <= totalBudget; budget++) {
                // Don't allocate to this category
                dp[i][budget] = dp[i - 1][budget];
                
                // Try allocating to this category
                int historicalAmount = (int) historical[i - 1];
                if (budget >= historicalAmount) {
                    double value = historicalAmount * priorities[i - 1];
                    dp[i][budget] = Math.max(dp[i][budget], dp[i - 1][budget - historicalAmount] + value);
                }
            }
        }
        
        // Backtrack to find optimal allocation
        Map<String, Double> result = new HashMap<>();
        int budget = (int) totalBudget;
        
        for (int i = n; i > 0; i--) {
            int historicalAmount = (int) historical[i - 1];
            if (budget >= historicalAmount && 
                dp[i][budget] == dp[i - 1][budget - historicalAmount] + historicalAmount * priorities[i - 1]) {
                result.put(categories.get(i - 1), (double) historicalAmount);
                budget -= historicalAmount;
            } else {
                result.put(categories.get(i - 1), 0.0);
            }
        }
        
        return result;
    }
    
    /**
     * Use Heap (Priority Queue) for budget recommendations
     */
    public List<String> getBudgetRecommendations(String userId) {
        com.example.mymoney.service.DataService dataService = com.example.mymoney.service.DataService.getInstance();
        
        // Get current month spending
        Map<String, Double> currentSpending = dataService.getCurrentMonthSpending(userId);
        Budget budget = getBudget(userId);
        
        // Use priority queue to find over-budget categories
        PriorityQueue<Map.Entry<String, Double>> overBudget = new PriorityQueue<>(
            (a, b) -> Double.compare(b.getValue() - budget.getCategoryBudget(a.getKey()), 
                                   a.getValue() - budget.getCategoryBudget(b.getKey()))
        );
        
        for (Map.Entry<String, Double> entry : currentSpending.entrySet()) {
            String category = entry.getKey();
            double spent = entry.getValue();
            double budgeted = budget.getCategoryBudget(category);
            
            if (spent > budgeted) {
                overBudget.offer(entry);
            }
        }
        
        List<String> recommendations = new ArrayList<>();
        while (!overBudget.isEmpty() && recommendations.size() < 5) {
            Map.Entry<String, Double> entry = overBudget.poll();
            double overspend = entry.getValue() - budget.getCategoryBudget(entry.getKey());
            recommendations.add(String.format("%s: Over budget by $%.2f", entry.getKey(), overspend));
        }
        
        return recommendations;
    }
}
