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
    
    // ✅ New: Update existing transaction
    public void updateTransaction(Transaction transaction) {
        try {
            String query = "UPDATE transactions SET type = ?, amount = ?, category = ?, notes = ?, transaction_date = ? WHERE id = ? AND user_id = ?";
            PreparedStatement stmt = dbManager.getConnection().prepareStatement(query);
            
            stmt.setString(1, transaction.getType().name());
            stmt.setDouble(2, transaction.getAmount());
            stmt.setString(3, transaction.getCategory());
            stmt.setString(4, transaction.getNotes());
            stmt.setDate(5, java.sql.Date.valueOf(transaction.getDate()));
            stmt.setString(6, transaction.getId());
            stmt.setString(7, transaction.getUserId());
            
            int rowsUpdated = stmt.executeUpdate();
            stmt.close();
            
            if (rowsUpdated > 0) {
                System.out.println("✅ Transaction updated: " + transaction.getCategory() + " - $" + transaction.getAmount());
            } else {
                System.out.println("⚠️ No transaction updated (ID not found or permission denied)");
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to update transaction!");
            e.printStackTrace();
        }
    }
    
    // ✅ New: Delete transaction
    public void deleteTransaction(String transactionId, String userId) {
        try {
            String query = "DELETE FROM transactions WHERE id = ? AND user_id = ?";
            PreparedStatement stmt = dbManager.getConnection().prepareStatement(query);
            stmt.setString(1, transactionId);
            stmt.setString(2, userId);
            
            int rowsDeleted = stmt.executeUpdate();
            stmt.close();
            
            if (rowsDeleted > 0) {
                System.out.println("✅ Transaction deleted: " + transactionId);
            } else {
                System.out.println("⚠️ No transaction deleted (ID not found or permission denied)");
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to delete transaction!");
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
                // ✅ Fixed: Sanitize all fields to prevent CSV injection
                String sanitizedCategory = sanitizeCSVField(transaction.getCategory());
                String sanitizedNotes = sanitizeCSVField(transaction.getNotes());
                
                writer.write(String.format("\"%s\",\"%s\",\"%s\",%.2f,\"%s\"\n",
                        transaction.getFormattedDate(),
                        transaction.getType(),
                        sanitizedCategory,
                        transaction.getAmount(),
                        sanitizedNotes));
            }
        }
    }
    
    // ✅ CSV Injection Prevention: Escape dangerous characters
    private String sanitizeCSVField(String field) {
        if (field == null) return "";
        
        // Escape double quotes by doubling them (RFC 4180)
        String sanitized = field.replace("\"", "\"\"");
        
        // Prevent CSV injection by neutralizing formula characters
        if (sanitized.length() > 0) {
            char firstChar = sanitized.charAt(0);
            if (firstChar == '=' || firstChar == '+' || firstChar == '-' || 
                firstChar == '@' || firstChar == '\t' || firstChar == '\r') {
                sanitized = "'" + sanitized; // Prefix with single quote to treat as text
            }
        }
        
        return sanitized;
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
}
