package com.example.mymoney.model;

public class FinancialMetrics {
    private double totalIncome;
    private double totalExpenses;
    private double currentBalance;
    private double savingsRate;

    public FinancialMetrics(double totalIncome, double totalExpenses) {
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.currentBalance = totalIncome - totalExpenses;
        this.savingsRate = totalIncome > 0 ? ((totalIncome - totalExpenses) / totalIncome) * 100 : 0;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public double getSavingsRate() {
        return savingsRate;
    }

    public String getFormattedIncome() {
        return String.format("₱%.2f", totalIncome);
    }

    public String getFormattedExpenses() {
        return String.format("₱%.2f", totalExpenses);
    }

    public String getFormattedBalance() {
        return String.format("₱%.2f", currentBalance);
    }

    public String getFormattedSavingsRate() {
        return String.format("%.1f%%", savingsRate);
    }
}

