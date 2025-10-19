package com.example.mymoney.service;

import com.example.mymoney.database.DatabaseManager;
import com.example.mymoney.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthenticationService {
    private static AuthenticationService instance;
    private User currentUser;
    private final DatabaseManager dbManager;
    private final Map<String, LoginAttempt> loginAttempts = new HashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 15 * 60 * 1000; // 15 minutes

    private AuthenticationService() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public static synchronized AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    public boolean register(String username, String password) {
        try {
            // Check if user already exists
            String checkQuery = "SELECT id FROM users WHERE username = ?";
            PreparedStatement checkStmt = dbManager.getConnection().prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery(); // ✅ Fixed: Now actually executes the query
            
            if (rs.next()) {
                rs.close();
                checkStmt.close();
                return false; // User already exists
            }
            rs.close();
            checkStmt.close();

            // Create new user
            String salt = generateSalt();
            String passwordHash = hashPassword(password, salt);
            String userId = UUID.randomUUID().toString();

            String insertQuery = "INSERT INTO users (id, username, password_hash, salt) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = dbManager.getConnection().prepareStatement(insertQuery);
            stmt.setString(1, userId);
            stmt.setString(2, username);
            stmt.setString(3, passwordHash);
            stmt.setString(4, salt);
            
            int result = stmt.executeUpdate();
            stmt.close();
            
            System.out.println("✅ User registered: " + username);
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Registration failed!");
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String username, String password) {
        // ✅ Enhanced: Check for account lockout
        if (isAccountLocked(username)) {
            System.out.println("❌ Account locked for: " + username);
            return false;
        }
        
        try {
            String query = "SELECT id, username, password_hash, salt FROM users WHERE username = ?";
            PreparedStatement stmt = dbManager.getConnection().prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String id = rs.getString("id");
                String dbUsername = rs.getString("username");
                String dbPasswordHash = rs.getString("password_hash");
                String dbSalt = rs.getString("salt");

                String passwordHash = hashPassword(password, dbSalt);
                
                if (passwordHash.equals(dbPasswordHash)) {
                    currentUser = new User(id, dbUsername, dbPasswordHash, dbSalt);
                    resetLoginAttempts(username); // ✅ Clear failed attempts on success
                    stmt.close();
                    System.out.println("✅ User logged in: " + username);
                    return true;
                }
            }
            
            stmt.close();
            recordFailedLogin(username); // ✅ Track failed attempt
            System.out.println("❌ Login failed for: " + username);
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Login error!");
            e.printStackTrace();
            return false;
        }
    }

    public void logout() {
        currentUser = null;
        System.out.println("✅ User logged out");
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String saltedPassword = password + salt;
            byte[] hash = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            
            // ✅ Enhanced: Apply multiple iterations for better security (10,000 rounds)
            for (int i = 0; i < 9999; i++) {
                hash = digest.digest(hash);
            }
            
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
    
    // ✅ Brute-force protection methods
    private void recordFailedLogin(String username) {
        LoginAttempt attempt = loginAttempts.getOrDefault(username, new LoginAttempt());
        attempt.incrementAttempts();
        loginAttempts.put(username, attempt);
    }
    
    private void resetLoginAttempts(String username) {
        loginAttempts.remove(username);
    }
    
    private boolean isAccountLocked(String username) {
        LoginAttempt attempt = loginAttempts.get(username);
        if (attempt == null) return false;
        
        if (attempt.getAttempts() >= MAX_ATTEMPTS) {
            long timeSinceLock = System.currentTimeMillis() - attempt.getFirstAttemptTime();
            if (timeSinceLock < LOCKOUT_DURATION_MS) {
                return true;
            } else {
                // Lockout expired, reset
                loginAttempts.remove(username);
                return false;
            }
        }
        return false;
    }
    
    public long getRemainingLockoutTime(String username) {
        LoginAttempt attempt = loginAttempts.get(username);
        if (attempt == null || attempt.getAttempts() < MAX_ATTEMPTS) return 0;
        
        long elapsed = System.currentTimeMillis() - attempt.getFirstAttemptTime();
        long remaining = LOCKOUT_DURATION_MS - elapsed;
        return remaining > 0 ? remaining / 1000 : 0; // Return seconds
    }
    
    // Inner class for tracking login attempts
    private static class LoginAttempt {
        private int attempts = 0;
        private long firstAttemptTime = System.currentTimeMillis();
        
        public void incrementAttempts() {
            attempts++;
        }
        
        public int getAttempts() {
            return attempts;
        }
        
        public long getFirstAttemptTime() {
            return firstAttemptTime;
        }
    }
}
