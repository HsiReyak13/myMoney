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
import java.util.UUID;

public class AuthenticationService {
    private static AuthenticationService instance;
    private User currentUser;
    private final DatabaseManager dbManager;

    private AuthenticationService() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public static AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    public boolean register(String username, String password) {
        try {
            String checkQuery = "SELECT id FROM users WHERE username = ?";
            PreparedStatement checkStmt = dbManager.getConnection().prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.getResultSet();
            
            if (rs != null && rs.next()) {
                checkStmt.close();
                return false; // User already exists
            }
            checkStmt.close();

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
            
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Registration failed!");
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String username, String password) {
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
                    stmt.close();
                    return true;
                }
            }
            
            stmt.close();
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Login error!");
            e.printStackTrace();
            return false;
        }
    }

    public void logout() {
        currentUser = null;
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
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
