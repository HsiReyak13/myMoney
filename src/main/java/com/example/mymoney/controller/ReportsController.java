package com.example.mymoney.controller;

import com.example.mymoney.model.FinancialMetrics;
import com.example.mymoney.service.AuthenticationService;
import com.example.mymoney.service.DataService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.YearMonth;
import java.util.Map;
import java.util.TreeMap;

public class ReportsController {
    private final AuthenticationService authService;
    private final DataService dataService;
    private final Stage stage;

    public ReportsController(Stage stage) {
        this.authService = AuthenticationService.getInstance();
        this.dataService = DataService.getInstance();
        this.stage = stage;
    }

    public ScrollPane createReportsView() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_CENTER);

        Text title = new Text("Financial Reports");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-fill: white;");

        // Charts section
        HBox chartsSection = createChartsSection();

        // Financial insights
        VBox insightsSection = createFinancialInsights();

        content.getChildren().addAll(title, chartsSection, insightsSection);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        return scrollPane;
    }

    private HBox createChartsSection() {
        HBox section = new HBox(20);
        section.setAlignment(Pos.TOP_CENTER);
        section.setMaxWidth(1400);

        // Spending by Category (Pie Chart)
        VBox pieChartBox = createSpendingPieChart();
        HBox.setHgrow(pieChartBox, Priority.ALWAYS);

        // Balance & Expenses Trend (Line Chart)
        VBox lineChartBox = createBalanceTrendChart();
        HBox.setHgrow(lineChartBox, Priority.ALWAYS);

        section.getChildren().addAll(pieChartBox, lineChartBox);
        return section;
    }

    private VBox createSpendingPieChart() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(25));
        box.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 15;");
        box.setPrefWidth(600);

        Text title = new Text("Spending by Category");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: white;");

        PieChart pieChart = new PieChart();
        pieChart.setTitle("");
        pieChart.setLegendVisible(true);
        pieChart.setPrefHeight(350);
        pieChart.setStyle("-fx-background-color: transparent;");

        String userId = authService.getCurrentUser().getId();
        Map<String, Double> categorySpending = dataService.getCategorySpending(userId);

        if (categorySpending.isEmpty()) {
            Label noDataLabel = new Label("No expense data available");
            noDataLabel.setStyle("-fx-text-fill: #a8dadc; -fx-font-size: 14px;");
            box.getChildren().addAll(title, noDataLabel);
        } else {
            double total = categorySpending.values().stream().mapToDouble(Double::doubleValue).sum();
            
            for (Map.Entry<String, Double> entry : categorySpending.entrySet()) {
                double percentage = (entry.getValue() / total) * 100;
                PieChart.Data data = new PieChart.Data(
                    entry.getKey() + String.format(" %.0f%%", percentage),
                    entry.getValue()
                );
                pieChart.getData().add(data);
            }

            // Legend with amounts
            GridPane legend = new GridPane();
            legend.setHgap(40);
            legend.setVgap(8);
            legend.setPadding(new Insets(15, 0, 0, 0));

            int row = 0, col = 0;
            for (Map.Entry<String, Double> entry : categorySpending.entrySet()) {
                HBox legendItem = new HBox(10);
                legendItem.setAlignment(Pos.CENTER_LEFT);

                Label colorBox = new Label("●");
                colorBox.setStyle("-fx-text-fill: " + getCategoryColor(entry.getKey()) + "; -fx-font-size: 16px;");

                Label categoryLabel = new Label(entry.getKey());
                categoryLabel.setStyle("-fx-text-fill: #a8dadc; -fx-font-size: 13px;");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Label amountLabel = new Label(String.format("$%.0f", entry.getValue()));
                amountLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");

                legendItem.getChildren().addAll(colorBox, categoryLabel, spacer, amountLabel);
                legend.add(legendItem, col, row);

                col++;
                if (col >= 2) {
                    col = 0;
                    row++;
                }
            }

            box.getChildren().addAll(title, pieChart, legend);
        }

        return box;
    }

    private VBox createBalanceTrendChart() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(25));
        box.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 15;");
        box.setPrefWidth(600);

        Text title = new Text("Balance & Expenses Trend");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: white;");

        // Create axes
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

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("");
        lineChart.setLegendVisible(true);
        lineChart.setCreateSymbols(true);
        lineChart.setPrefHeight(350);
        lineChart.setStyle("-fx-background-color: transparent;");

        String userId = authService.getCurrentUser().getId();
        Map<YearMonth, Double> monthlyIncome = dataService.getMonthlyIncome(userId);
        Map<YearMonth, Double> monthlyExpenses = dataService.getMonthlyExpenses(userId);

        XYChart.Series<Number, Number> balanceSeries = new XYChart.Series<>();
        balanceSeries.setName("balance");

        XYChart.Series<Number, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("expenses");

        if (monthlyIncome.isEmpty() && monthlyExpenses.isEmpty()) {
            // Show empty chart
            for (int i = 1; i <= 6; i++) {
                balanceSeries.getData().add(new XYChart.Data<>(i, 0));
                expenseSeries.getData().add(new XYChart.Data<>(i, 0));
            }
        } else {
            TreeMap<YearMonth, Double> sortedIncome = new TreeMap<>(monthlyIncome);
            TreeMap<YearMonth, Double> sortedExpenses = new TreeMap<>(monthlyExpenses);

            TreeMap<YearMonth, Double> allMonths = new TreeMap<>();
            sortedIncome.forEach((month, amount) -> allMonths.put(month, amount));
            sortedExpenses.forEach((month, amount) -> allMonths.putIfAbsent(month, 0.0));

            double runningBalance = 0;
            for (Map.Entry<YearMonth, Double> entry : allMonths.entrySet()) {
                YearMonth month = entry.getKey();
                double income = sortedIncome.getOrDefault(month, 0.0);
                double expense = sortedExpenses.getOrDefault(month, 0.0);
                runningBalance += (income - expense);

                balanceSeries.getData().add(new XYChart.Data<>(month.getMonthValue(), runningBalance));
                expenseSeries.getData().add(new XYChart.Data<>(month.getMonthValue(), expense));
            }
        }

        lineChart.getData().addAll(expenseSeries, balanceSeries);

        // Average stats
        FinancialMetrics metrics = dataService.calculateMetrics(userId);
        int monthCount = Math.max(monthlyIncome.size(), monthlyExpenses.size());
        monthCount = monthCount > 0 ? monthCount : 1;

        HBox stats = new HBox(60);
        stats.setAlignment(Pos.CENTER);
        stats.setPadding(new Insets(15, 0, 0, 0));

        VBox avgBalance = new VBox(5);
        avgBalance.setAlignment(Pos.CENTER);
        Label avgBalanceTitle = new Label("Average Monthly Balance");
        avgBalanceTitle.setStyle("-fx-text-fill: #a8dadc; -fx-font-size: 12px;");
        Label avgBalanceValue = new Label(String.format("$%.0f", metrics.getCurrentBalance() / monthCount));
        avgBalanceValue.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        avgBalance.getChildren().addAll(avgBalanceTitle, avgBalanceValue);

        VBox avgExpenses = new VBox(5);
        avgExpenses.setAlignment(Pos.CENTER);
        Label avgExpensesTitle = new Label("Average Monthly Expenses");
        avgExpensesTitle.setStyle("-fx-text-fill: #a8dadc; -fx-font-size: 12px;");
        Label avgExpensesValue = new Label(String.format("$%.0f", metrics.getTotalExpenses() / monthCount));
        avgExpensesValue.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        avgExpenses.getChildren().addAll(avgExpensesTitle, avgExpensesValue);

        stats.getChildren().addAll(avgBalance, avgExpenses);

        box.getChildren().addAll(title, lineChart, stats);
        return box;
    }

    private VBox createFinancialInsights() {
        VBox section = new VBox(20);
        section.setMaxWidth(1400);
        section.setPadding(new Insets(25));
        section.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 15;");

        Text sectionTitle = new Text("Financial Insights");
        sectionTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: white;");

        String userId = authService.getCurrentUser().getId();
        Map<String, Double> categorySpending = dataService.getCategorySpending(userId);
        FinancialMetrics metrics = dataService.calculateMetrics(userId);

        GridPane insights = new GridPane();
        insights.setHgap(60);
        insights.setVgap(20);
        insights.setAlignment(Pos.CENTER);

        // Highest Expense Category
        VBox highestExpense = createInsightCard(
            "Highest Expense Category",
            getHighestCategory(categorySpending),
            String.format("$%.0f", getHighestAmount(categorySpending)),
            "#06ffa5"
        );

        // Growth Rate
        VBox growthRate = createInsightCard(
            "Growth Rate (6 months)",
            "Balance Increase",
            "+46.5%",
            "#06ffa5"
        );

        // Savings Goal
        VBox savingsGoal = createInsightCard(
            "Current Savings Goal",
            "Emergency Fund",
            "65% Complete",
            "#f4a261"
        );

        insights.add(highestExpense, 0, 0);
        insights.add(growthRate, 1, 0);
        insights.add(savingsGoal, 2, 0);

        section.getChildren().addAll(sectionTitle, insights);
        return section;
    }

    private VBox createInsightCard(String title, String label, String value, String valueColor) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(250);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #a8dadc; -fx-font-size: 13px;");

        Label mainLabel = new Label(label);
        mainLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: " + valueColor + "; -fx-font-size: 20px; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, mainLabel, valueLabel);
        return card;
    }

    private String getHighestCategory(Map<String, Double> categorySpending) {
        return categorySpending.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("None");
    }

    private double getHighestAmount(Map<String, Double> categorySpending) {
        return categorySpending.values().stream()
            .max(Double::compare)
            .orElse(0.0);
    }

    private String getCategoryColor(String category) {
        return switch (category) {
            case "Groceries", "Food" -> "#06ffa5";
            case "Shopping" -> "#4dabf7";
            case "Transportation" -> "#f4a261";
            case "Entertainment" -> "#9775fa";
            case "Healthcare" -> "#ff6b9d";
            case "Utilities" -> "#20c997";
            default -> "#a8dadc";
        };
    }
}
