package com.example.mymoney.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Transaction {
    private final String id;
    private final String userId;
    private final TransactionType type;
    private final double amount;
    private final String category;
    private final String notes;
    private final LocalDate date;

    public enum TransactionType {
        INCOME, EXPENSE
    }

    public Transaction(String userId, TransactionType type, double amount, String category, String notes, LocalDate date) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.notes = notes;
        this.date = date;
    }

    public Transaction(String id, String userId, TransactionType type, double amount, String category, String notes, LocalDate date) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.notes = notes;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }
}

