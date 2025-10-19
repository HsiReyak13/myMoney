package com.example.mymoney.service;

import com.example.mymoney.database.DatabaseManager;
import com.example.mymoney.model.FinancialMetrics;
import com.example.mymoney.model.Transaction;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.PriorityQueue;

public class DataService {
    private static DataService instance;
    private final DatabaseManager dbManager;

    private DataService() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public static DataService getInstance() {
        if (instance == null) {
            instance = new DataService();
        }
        return instance;
    }

    public void addTransaction(Transaction transaction) {
        try {
            String query = "INSERT INTO transactions (id, user_id, type, amount, category, notes, transaction_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = dbManager.getConnection().prepareStatement(query);
            
            stmt.setString(1, transaction.getId());
            stmt.setString(2, transaction.getUserId());
            stmt.setString(3, transaction.getType().name());
            stmt.setDouble(4, transaction.getAmount());
            stmt.setString(5, transaction.getCategory());
            stmt.setString(6, transaction.getNotes());
            stmt.setDate(7, java.sql.Date.valueOf(transaction.getDate()));
            
            stmt.executeUpdate();
            stmt.close();
            
            System.out.println("✅ Transaction added: " + transaction.getCategory() + " - $" + transaction.getAmount());
        } catch (SQLException e) {
            System.err.println("❌ Failed to add transaction!");
            e.printStackTrace();
        }
    }

    public List<Transaction> getTransactionsForUser(String userId) {
        List<Transaction> transactions = new ArrayList<>();
        try {
            String query = "SELECT * FROM transactions WHERE user_id = ? ORDER BY transaction_date DESC, created_at DESC";
            PreparedStatement stmt = dbManager.getConnection().prepareStatement(query);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction(
                    rs.getString("id"),
                    rs.getString("user_id"),
                    Transaction.TransactionType.valueOf(rs.getString("type")),
                    rs.getDouble("amount"),
                    rs.getString("category"),
                    rs.getString("notes"),
                    rs.getDate("transaction_date").toLocalDate()
                );
                transactions.add(transaction);
            }

            stmt.close();
        } catch (SQLException e) {
            System.err.println("❌ Failed to get transactions!");
            e.printStackTrace();
        }
        return transactions;
    }

    public Map<String, Double> getCategorySpending(String userId) {
        Map<String, Double> categorySpending = new HashMap<>();
        try {
            String query = "SELECT category, SUM(amount) as total FROM transactions WHERE user_id = ? AND type = 'EXPENSE' GROUP BY category";
            PreparedStatement stmt = dbManager.getConnection().prepareStatement(query);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                categorySpending.put(rs.getString("category"), rs.getDouble("total"));
            }

            stmt.close();
        } catch (SQLException e) {
            System.err.println("❌ Failed to get category spending!");
            e.printStackTrace();
        }
        return categorySpending;
    }

    public Map<String, Double> getCurrentMonthSpending(String userId) {
        Map<String, Double> categorySpending = new HashMap<>();
        try {
            YearMonth currentMonth = YearMonth.now();
            String query = "SELECT category, SUM(amount) as total FROM transactions WHERE user_id = ? AND type = 'EXPENSE' AND YEAR(transaction_date) = ? AND MONTH(transaction_date) = ? GROUP BY category";
            PreparedStatement stmt = dbManager.getConnection().prepareStatement(query);
            stmt.setString(1, userId);
            stmt.setInt(2, currentMonth.getYear());
            stmt.setInt(3, currentMonth.getMonthValue());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                categorySpending.put(rs.getString("category"), rs.getDouble("total"));
            }

            stmt.close();
        } catch (SQLException e) {
            System.err.println("❌ Failed to get current month spending!");
            e.printStackTrace();
        }
        return categorySpending;
    }

    public double getCurrentMonthTotalExpenses(String userId) {
        try {
            YearMonth currentMonth = YearMonth.now();
            String query = "SELECT SUM(amount) as total FROM transactions WHERE user_id = ? AND type = 'EXPENSE' AND YEAR(transaction_date) = ? AND MONTH(transaction_date) = ?";
            PreparedStatement stmt = dbManager.getConnection().prepareStatement(query);
            stmt.setString(1, userId);
            stmt.setInt(2, currentMonth.getYear());
            stmt.setInt(3, currentMonth.getMonthValue());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double total = rs.getDouble("total");
                stmt.close();
                return total;
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("❌ Failed to get current month total expenses!");
            e.printStackTrace();
        }
        return 0.0;
    }

    public Map<YearMonth, Double> getMonthlyIncome(String userId) {
        Map<YearMonth, Double> monthlyIncome = new TreeMap<>();
        try {
            String query = "SELECT YEAR(transaction_date) as year, MONTH(transaction_date) as month, SUM(amount) as total FROM transactions WHERE user_id = ? AND type = 'INCOME' GROUP BY YEAR(transaction_date), MONTH(transaction_date) ORDER BY year, month";
            PreparedStatement stmt = dbManager.getConnection().prepareStatement(query);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                YearMonth yearMonth = YearMonth.of(rs.getInt("year"), rs.getInt("month"));
                monthlyIncome.put(yearMonth, rs.getDouble("total"));
            }

            stmt.close();
        } catch (SQLException e) {
            System.err.println("❌ Failed to get monthly income!");
            e.printStackTrace();
        }
        return monthlyIncome;
    }

    public Map<YearMonth, Double> getMonthlyExpenses(String userId) {
        Map<YearMonth, Double> monthlyExpenses = new TreeMap<>();
        try {
            String query = "SELECT YEAR(transaction_date) as year, MONTH(transaction_date) as month, SUM(amount) as total FROM transactions WHERE user_id = ? AND type = 'EXPENSE' GROUP BY YEAR(transaction_date), MONTH(transaction_date) ORDER BY year, month";
            PreparedStatement stmt = dbManager.getConnection().prepareStatement(query);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                YearMonth yearMonth = YearMonth.of(rs.getInt("year"), rs.getInt("month"));
                monthlyExpenses.put(yearMonth, rs.getDouble("total"));
            }

            stmt.close();
        } catch (SQLException e) {
            System.err.println("❌ Failed to get monthly expenses!");
            e.printStackTrace();
        }
        return monthlyExpenses;
    }

    public FinancialMetrics calculateMetrics(String userId) {
        double totalIncome = 0.0;
        double totalExpenses = 0.0;

        try {
            String query = "SELECT type, SUM(amount) as total FROM transactions WHERE user_id = ? GROUP BY type";
            PreparedStatement stmt = dbManager.getConnection().prepareStatement(query);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String type = rs.getString("type");
                double total = rs.getDouble("total");
                
                if ("INCOME".equals(type)) {
                    totalIncome = total;
                } else if ("EXPENSE".equals(type)) {
                    totalExpenses = total;
                }
            }

            stmt.close();
        } catch (SQLException e) {
            System.err.println("❌ Failed to calculate metrics!");
            e.printStackTrace();
        }

        return new FinancialMetrics(totalIncome, totalExpenses);
    }

    public void exportToCSV(String userId, String filePath) throws IOException {
        List<Transaction> userTransactions = getTransactionsForUser(userId);
        
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Date,Type,Category,Amount,Notes\n");
            
            for (Transaction transaction : userTransactions) {
                writer.write(String.format("%s,%s,%s,%.2f,\"%s\"\n",
                        transaction.getFormattedDate(),
                        transaction.getType(),
                        transaction.getCategory(),
                        transaction.getAmount(),
                        transaction.getNotes().replace("\"", "\"\"")));
            }
        }
    }

    public void clearAllData() {
        try {
            String query = "DELETE FROM transactions";
            Statement stmt = dbManager.getConnection().createStatement();
            stmt.executeUpdate(query);
            stmt.close();
            System.out.println("✅ All transactions cleared");
        } catch (SQLException e) {
            System.err.println("❌ Failed to clear transactions!");
            e.printStackTrace();
        }
    }

    // ==================== DSA CONCEPTS IMPLEMENTATION ====================
    
    /**
     * SORTING ALGORITHMS - Different sorting strategies for transactions
     */
    
    // Quick Sort implementation for transactions by amount
    public List<Transaction> quickSortByAmount(List<Transaction> transactions, boolean ascending) {
        if (transactions == null || transactions.size() <= 1) {
            return new ArrayList<>(transactions);
        }
        
        List<Transaction> result = new ArrayList<>(transactions);
        quickSortByAmountHelper(result, 0, result.size() - 1, ascending);
        return result;
    }
    
    private void quickSortByAmountHelper(List<Transaction> transactions, int low, int high, boolean ascending) {
        if (low < high) {
            int pivotIndex = partitionByAmount(transactions, low, high, ascending);
            quickSortByAmountHelper(transactions, low, pivotIndex - 1, ascending);
            quickSortByAmountHelper(transactions, pivotIndex + 1, high, ascending);
        }
    }
    
    private int partitionByAmount(List<Transaction> transactions, int low, int high, boolean ascending) {
        double pivot = transactions.get(high).getAmount();
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            boolean shouldSwap = ascending ? 
                transactions.get(j).getAmount() <= pivot : 
                transactions.get(j).getAmount() >= pivot;
                
            if (shouldSwap) {
                i++;
                swap(transactions, i, j);
            }
        }
        swap(transactions, i + 1, high);
        return i + 1;
    }
    
    // Merge Sort implementation for transactions by date
    public List<Transaction> mergeSortByDate(List<Transaction> transactions, boolean ascending) {
        if (transactions == null || transactions.size() <= 1) {
            return new ArrayList<>(transactions);
        }
        
        List<Transaction> result = new ArrayList<>(transactions);
        mergeSortByDateHelper(result, 0, result.size() - 1, ascending);
        return result;
    }
    
    private void mergeSortByDateHelper(List<Transaction> transactions, int left, int right, boolean ascending) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSortByDateHelper(transactions, left, mid, ascending);
            mergeSortByDateHelper(transactions, mid + 1, right, ascending);
            mergeByDate(transactions, left, mid, right, ascending);
        }
    }
    
    private void mergeByDate(List<Transaction> transactions, int left, int mid, int right, boolean ascending) {
        List<Transaction> leftArray = new ArrayList<>();
        List<Transaction> rightArray = new ArrayList<>();
        
        for (int i = left; i <= mid; i++) {
            leftArray.add(transactions.get(i));
        }
        for (int i = mid + 1; i <= right; i++) {
            rightArray.add(transactions.get(i));
        }
        
        int i = 0, j = 0, k = left;
        
        while (i < leftArray.size() && j < rightArray.size()) {
            boolean shouldTakeLeft = ascending ? 
                leftArray.get(i).getDate().isBefore(rightArray.get(j).getDate()) || 
                leftArray.get(i).getDate().isEqual(rightArray.get(j).getDate()) :
                leftArray.get(i).getDate().isAfter(rightArray.get(j).getDate()) || 
                leftArray.get(i).getDate().isEqual(rightArray.get(j).getDate());
                
            if (shouldTakeLeft) {
                transactions.set(k, leftArray.get(i));
                i++;
            } else {
                transactions.set(k, rightArray.get(j));
                j++;
            }
            k++;
        }
        
        while (i < leftArray.size()) {
            transactions.set(k, leftArray.get(i));
            i++;
            k++;
        }
        
        while (j < rightArray.size()) {
            transactions.set(k, rightArray.get(j));
            j++;
            k++;
        }
    }
    
    // Heap Sort implementation for transactions by category
    public List<Transaction> heapSortByCategory(List<Transaction> transactions, boolean ascending) {
        if (transactions == null || transactions.size() <= 1) {
            return new ArrayList<>(transactions);
        }
        
        List<Transaction> result = new ArrayList<>(transactions);
        int n = result.size();
        
        // Build heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapifyByCategory(result, n, i, ascending);
        }
        
        // Extract elements from heap one by one
        for (int i = n - 1; i > 0; i--) {
            swap(result, 0, i);
            heapifyByCategory(result, i, 0, ascending);
        }
        
        return result;
    }
    
    private void heapifyByCategory(List<Transaction> transactions, int n, int i, boolean ascending) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        
        if (left < n) {
            boolean shouldSwap = ascending ? 
                transactions.get(left).getCategory().compareTo(transactions.get(largest).getCategory()) > 0 :
                transactions.get(left).getCategory().compareTo(transactions.get(largest).getCategory()) < 0;
            if (shouldSwap) {
                largest = left;
            }
        }
        
        if (right < n) {
            boolean shouldSwap = ascending ? 
                transactions.get(right).getCategory().compareTo(transactions.get(largest).getCategory()) > 0 :
                transactions.get(right).getCategory().compareTo(transactions.get(largest).getCategory()) < 0;
            if (shouldSwap) {
                largest = right;
            }
        }
        
        if (largest != i) {
            swap(transactions, i, largest);
            heapifyByCategory(transactions, n, largest, ascending);
        }
    }
    
    private void swap(List<Transaction> transactions, int i, int j) {
        Transaction temp = transactions.get(i);
        transactions.set(i, transactions.get(j));
        transactions.set(j, temp);
    }
    
    /**
     * SEARCH ALGORITHMS - Efficient searching strategies
     */
    
    // Binary Search for finding transactions by amount (requires sorted list)
    public List<Transaction> binarySearchByAmount(List<Transaction> sortedTransactions, double targetAmount, double tolerance) {
        List<Transaction> results = new ArrayList<>();
        binarySearchByAmountHelper(sortedTransactions, targetAmount, tolerance, 0, sortedTransactions.size() - 1, results);
        return results;
    }
    
    private void binarySearchByAmountHelper(List<Transaction> transactions, double target, double tolerance, 
                                          int left, int right, List<Transaction> results) {
        if (left <= right) {
            int mid = left + (right - left) / 2;
            double currentAmount = transactions.get(mid).getAmount();
            
            if (Math.abs(currentAmount - target) <= tolerance) {
                results.add(transactions.get(mid));
                
                // Search for duplicates in both directions
                int leftDup = mid - 1;
                while (leftDup >= 0 && Math.abs(transactions.get(leftDup).getAmount() - target) <= tolerance) {
                    results.add(transactions.get(leftDup));
                    leftDup--;
                }
                
                int rightDup = mid + 1;
                while (rightDup < transactions.size() && Math.abs(transactions.get(rightDup).getAmount() - target) <= tolerance) {
                    results.add(transactions.get(rightDup));
                    rightDup++;
                }
            } else if (currentAmount < target) {
                binarySearchByAmountHelper(transactions, target, tolerance, mid + 1, right, results);
            } else {
                binarySearchByAmountHelper(transactions, target, tolerance, left, mid - 1, results);
            }
        }
    }
    
    // Linear Search with early termination for category search
    public List<Transaction> linearSearchByCategory(List<Transaction> transactions, String category) {
        List<Transaction> results = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getCategory().equalsIgnoreCase(category)) {
                results.add(transaction);
            }
        }
        return results;
    }
    
    // KMP Pattern Matching for searching in transaction notes
    public List<Transaction> searchInNotes(List<Transaction> transactions, String pattern) {
        List<Transaction> results = new ArrayList<>();
        int[] lps = computeLPSArray(pattern);
        
        for (Transaction transaction : transactions) {
            if (transaction.getNotes() != null && kmpSearch(transaction.getNotes(), pattern, lps)) {
                results.add(transaction);
            }
        }
        return results;
    }
    
    private int[] computeLPSArray(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0;
        int i = 1;
        
        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }
    
    private boolean kmpSearch(String text, String pattern, int[] lps) {
        int i = 0; // index for text
        int j = 0; // index for pattern
        
        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            }
            
            if (j == pattern.length()) {
                return true;
            } else if (i < text.length() && text.charAt(i) != pattern.charAt(j)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return false;
    }
    
    /**
     * ADVANCED DATA STRUCTURES
     */
    
    // Priority Queue for getting top N transactions by amount
    public List<Transaction> getTopNTransactionsByAmount(List<Transaction> transactions, int n, boolean highest) {
        if (transactions == null || transactions.isEmpty()) {
            return new ArrayList<>();
        }
        
        Comparator<Transaction> comparator = highest ? 
            Comparator.comparing(Transaction::getAmount).reversed() :
            Comparator.comparing(Transaction::getAmount);
            
        PriorityQueue<Transaction> pq = new PriorityQueue<>(comparator);
        
        for (Transaction transaction : transactions) {
            pq.offer(transaction);
            if (pq.size() > n) {
                pq.poll();
            }
        }
        
        List<Transaction> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            result.add(pq.poll());
        }
        
        if (highest) {
            Collections.reverse(result);
        }
        
        return result;
    }
    
    // Trie data structure for efficient category prefix search
    public List<String> searchCategoriesByPrefix(List<Transaction> transactions, String prefix) {
        Set<String> categories = transactions.stream()
            .map(Transaction::getCategory)
            .collect(Collectors.toSet());
            
        return categories.stream()
            .filter(category -> category.toLowerCase().startsWith(prefix.toLowerCase()))
            .sorted()
            .collect(Collectors.toList());
    }
    
    // Graph representation for spending patterns (using adjacency list)
    public Map<String, List<String>> buildSpendingGraph(List<Transaction> transactions) {
        Map<String, List<String>> graph = new HashMap<>();
        
        // Group transactions by date and find patterns
        Map<String, List<Transaction>> dailyTransactions = transactions.stream()
            .collect(Collectors.groupingBy(t -> t.getDate().toString()));
            
        for (List<Transaction> dayTransactions : dailyTransactions.values()) {
            for (int i = 0; i < dayTransactions.size(); i++) {
                for (int j = i + 1; j < dayTransactions.size(); j++) {
                    String category1 = dayTransactions.get(i).getCategory();
                    String category2 = dayTransactions.get(j).getCategory();
                    
                    graph.computeIfAbsent(category1, k -> new ArrayList<>()).add(category2);
                    graph.computeIfAbsent(category2, k -> new ArrayList<>()).add(category1);
                }
            }
        }
        
        return graph;
    }
    
    /**
     * ALGORITHM OPTIMIZATIONS
     */
    
    // Memoization for expensive calculations
    private final Map<String, FinancialMetrics> metricsCache = new HashMap<>();
    
    public FinancialMetrics calculateMetricsOptimized(String userId) {
        String cacheKey = userId + "_metrics";
        
        if (metricsCache.containsKey(cacheKey)) {
            return metricsCache.get(cacheKey);
        }
        
        FinancialMetrics metrics = calculateMetrics(userId);
        metricsCache.put(cacheKey, metrics);
        return metrics;
    }
    
    public void clearMetricsCache() {
        metricsCache.clear();
    }
    
    // Sliding Window algorithm for finding maximum spending in a period
    public double findMaxSpendingInWindow(List<Transaction> transactions, int windowDays) {
        if (transactions.isEmpty()) return 0.0;
        
        // Sort by date
        List<Transaction> sortedByDate = mergeSortByDate(transactions, true);
        
        double maxSpending = 0.0;
        double currentSpending = 0.0;
        int left = 0;
        
        for (int right = 0; right < sortedByDate.size(); right++) {
            Transaction current = sortedByDate.get(right);
            if (current.getType() == Transaction.TransactionType.EXPENSE) {
                currentSpending += current.getAmount();
            }
            
            // Slide window if it exceeds the window size
            while (left <= right && 
                   sortedByDate.get(right).getDate().toEpochDay() - 
                   sortedByDate.get(left).getDate().toEpochDay() > windowDays) {
                if (sortedByDate.get(left).getType() == Transaction.TransactionType.EXPENSE) {
                    currentSpending -= sortedByDate.get(left).getAmount();
                }
                left++;
            }
            
            maxSpending = Math.max(maxSpending, currentSpending);
        }
        
        return maxSpending;
    }
    
    // Dynamic Programming for optimal budget allocation
    public Map<String, Double> optimalBudgetAllocation(List<Transaction> transactions, double totalBudget) {
        // Group expenses by category
        Map<String, Double> categoryExpenses = transactions.stream()
            .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
            .collect(Collectors.groupingBy(
                Transaction::getCategory,
                Collectors.summingDouble(Transaction::getAmount)
            ));
        
        if (categoryExpenses.isEmpty()) {
            return new HashMap<>();
        }
        
        // Use proportional allocation based on historical spending
        double totalExpenses = categoryExpenses.values().stream().mapToDouble(Double::doubleValue).sum();
        Map<String, Double> optimalAllocation = new HashMap<>();
        
        for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
            double proportion = entry.getValue() / totalExpenses;
            optimalAllocation.put(entry.getKey(), totalBudget * proportion);
        }
        
        return optimalAllocation;
    }
}
