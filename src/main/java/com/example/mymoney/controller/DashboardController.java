package com.example.mymoney.controller;

import com.example.mymoney.model.FinancialMetrics;
import com.example.mymoney.model.Transaction;
import com.example.mymoney.service.AuthenticationService;
import com.example.mymoney.service.DataService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardController {
    private final AuthenticationService authService;
    private final DataService dataService;
    private VBox dashboardContent;
    private Label balanceValue, incomeValue, expensesValue, savingsValue;
    private LineChart<Number, Number> trendChart;

    public DashboardController() {
        this.authService = AuthenticationService.getInstance();
        this.dataService = DataService.getInstance();
    }

    public ScrollPane createDashboardView() {
        dashboardContent = new VBox(30);
        dashboardContent.setPadding(new Insets(30));
        dashboardContent.setAlignment(Pos.TOP_CENTER);

        GridPane metricsGrid = createMetricsCards();

        VBox chartSection = createTrendChart();

        dashboardContent.getChildren().addAll(metricsGrid, chartSection);

        ScrollPane scrollPane = new ScrollPane(dashboardContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        refresh();

        return scrollPane;
    }

    private GridPane createMetricsCards() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);
        grid.setMaxWidth(1400);

        VBox balanceCard = createMetricCard("Total Balance", "$0.00", "+0%", "metric-positive");
        balanceValue = (Label) ((VBox) balanceCard.getChildren().get(1)).getChildren().get(0);
        grid.add(balanceCard, 0, 0);

        VBox incomeCard = createMetricCard("Total Income", "$0.00", "+0%", "metric-positive");
        incomeValue = (Label) ((VBox) incomeCard.getChildren().get(1)).getChildren().get(0);
        grid.add(incomeCard, 1, 0);

        VBox expensesCard = createMetricCard("Total Expenses", "$0.00", "+0%", "metric-negative");
        expensesValue = (Label) ((VBox) expensesCard.getChildren().get(1)).getChildren().get(0);
        grid.add(expensesCard, 2, 0);

        VBox savingsCard = createMetricCard("Savings Rate", "0%", "+0%", "metric-positive");
        savingsValue = (Label) ((VBox) savingsCard.getChildren().get(1)).getChildren().get(0);
        grid.add(savingsCard, 3, 0);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            col.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(col);
        }

        return grid;
    }

    private VBox createMetricCard(String title, String value, String change, String valueStyle) {
        VBox card = new VBox(10);
        card.getStyleClass().add("metric-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefHeight(120);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPadding(new Insets(20));

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("metric-title");

        VBox valueBox = new VBox(5);
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().addAll("metric-value", valueStyle);

        Label changeLabel = new Label(change);
        changeLabel.setStyle("-fx-text-fill: #06ffa5; -fx-font-size: 12px;");

        valueBox.getChildren().addAll(valueLabel, changeLabel);

        card.getChildren().addAll(titleLabel, valueBox);
        return card;
    }

    private VBox createTrendChart() {
        VBox section = new VBox(15);
        section.setMaxWidth(1400);
        section.setPadding(new Insets(25));
        section.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 15;");

        Text title = new Text("Income vs Expenses Trend");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: white;");

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("");
        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(Number value) {
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                int monthIndex = value.intValue() - 1;
                if (monthIndex >= 0 && monthIndex < months.length) {
                    return months[monthIndex];
                }
                return "";
            }
        });

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("");

        trendChart = new LineChart<>(xAxis, yAxis);
        trendChart.setTitle("");
        trendChart.setLegendVisible(true);
        trendChart.setCreateSymbols(true);
        trendChart.setPrefHeight(400);
        trendChart.setStyle("-fx-background-color: transparent;");

        section.getChildren().addAll(title, trendChart);
        return section;
    }

    public void refresh() {
        String userId = authService.getCurrentUser().getId();
        
        FinancialMetrics metrics = dataService.calculateMetricsOptimized(userId);

        balanceValue.setText(metrics.getFormattedBalance());
        balanceValue.getStyleClass().clear();
        balanceValue.getStyleClass().addAll("metric-value", 
            metrics.getCurrentBalance() >= 0 ? "metric-positive" : "metric-negative");

        incomeValue.setText(metrics.getFormattedIncome());
        expensesValue.setText(metrics.getFormattedExpenses());
        savingsValue.setText(metrics.getFormattedSavingsRate());

        updateTrendChart(userId);
        
        showDSAInsights(userId);
    }
    
    private void showDSAInsights(String userId) {
        List<Transaction> allTransactions = dataService.getTransactionsForUser(userId);
        
        if (allTransactions.isEmpty()) return;
        
        long startTime = System.nanoTime();
        
        double maxSpending = dataService.findMaxSpendingInWindow(allTransactions, 7);
        
        Map<String, Double> optimalBudget = dataService.optimalBudgetAllocation(allTransactions, 5000.0);
        
        Map<String, List<String>> spendingGraph = dataService.buildSpendingGraph(allTransactions);
        
        List<Transaction> topExpenses = dataService.getTopNTransactionsByAmount(
            allTransactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                .collect(java.util.stream.Collectors.toList()), 
            3, true
        );
        
        long endTime = System.nanoTime();
        double analysisTime = (endTime - startTime) / 1_000_000.0;
        
    }

    private void updateTrendChart(String userId) {
        trendChart.getData().clear();

        Map<YearMonth, Double> monthlyIncome = dataService.getMonthlyIncome(userId);
        Map<YearMonth, Double> monthlyExpenses = dataService.getMonthlyExpenses(userId);

        // Get all months (last 6 months or available data)
        TreeMap<YearMonth, Double> sortedIncome = new TreeMap<>(monthlyIncome);
        TreeMap<YearMonth, Double> sortedExpenses = new TreeMap<>(monthlyExpenses);

        // If no data, show sample months
        if (sortedIncome.isEmpty() && sortedExpenses.isEmpty()) {
            XYChart.Series<Number, Number> incomeSeries = new XYChart.Series<>();
            incomeSeries.setName("income");
            for (int i = 1; i <= 6; i++) {
                incomeSeries.getData().add(new XYChart.Data<>(i, 0));
            }

            XYChart.Series<Number, Number> expenseSeries = new XYChart.Series<>();
            expenseSeries.setName("expenses");
            for (int i = 1; i <= 6; i++) {
                expenseSeries.getData().add(new XYChart.Data<>(i, 0));
            }

            trendChart.getData().addAll(expenseSeries, incomeSeries);
        } else {
            XYChart.Series<Number, Number> incomeSeries = new XYChart.Series<>();
            incomeSeries.setName("income");

            XYChart.Series<Number, Number> expenseSeries = new XYChart.Series<>();
            expenseSeries.setName("expenses");

            // Combine all months
            TreeMap<YearMonth, Double> allMonths = new TreeMap<>();
            sortedIncome.forEach((month, amount) -> allMonths.put(month, amount));
            sortedExpenses.forEach((month, amount) -> allMonths.putIfAbsent(month, 0.0));

            int index = 1;
            for (Map.Entry<YearMonth, Double> entry : allMonths.entrySet()) {
                YearMonth month = entry.getKey();
                double income = sortedIncome.getOrDefault(month, 0.0);
                double expense = sortedExpenses.getOrDefault(month, 0.0);

                incomeSeries.getData().add(new XYChart.Data<>(month.getMonthValue(), income));
                expenseSeries.getData().add(new XYChart.Data<>(month.getMonthValue(), expense));
                index++;
            }

            trendChart.getData().addAll(expenseSeries, incomeSeries);
        }
    }
}
