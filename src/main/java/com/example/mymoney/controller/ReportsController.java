package com.example.mymoney.controller;

import com.example.mymoney.model.FinancialMetrics;
import com.example.mymoney.model.Transaction;
import com.example.mymoney.service.AuthenticationService;
import com.example.mymoney.service.DataService;
import com.example.mymoney.service.BudgetService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class ReportsController {
    private final AuthenticationService authService;
    private final DataService dataService;
    private final BudgetService budgetService;
    private final Stage stage;

    public ReportsController(Stage stage) {
        this.authService = AuthenticationService.getInstance();
        this.dataService = DataService.getInstance();
        this.budgetService = BudgetService.getInstance();
        this.stage = stage;
    }

    public ScrollPane createReportsView() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_CENTER);

        Text title = new Text("Financial Reports & Analytics");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-fill: white;");

        VBox chartsSection = createEnhancedChartsSection();

        VBox insightsSection = createEnhancedFinancialInsights();

        content.getChildren().addAll(title, chartsSection, insightsSection);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        return scrollPane;
    }


    
    private VBox createEnhancedChartsSection() {
        VBox section = new VBox(20);
        section.setMaxWidth(1400);

        Text title = new Text("Enhanced Data Visualizations");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: white;");

        HBox topRow = new HBox(20);
        topRow.setAlignment(Pos.TOP_CENTER);
        
        VBox pieChartBox = createEnhancedSpendingPieChart();
        HBox.setHgrow(pieChartBox, Priority.ALWAYS);

        VBox barChartBox = createCategoryBarChart();
        HBox.setHgrow(barChartBox, Priority.ALWAYS);
        
        topRow.getChildren().addAll(pieChartBox, barChartBox);

        HBox bottomRow = new HBox(20);
        bottomRow.setAlignment(Pos.TOP_CENTER);
        
        VBox lineChartBox = createEnhancedBalanceTrendChart();
        HBox.setHgrow(lineChartBox, Priority.ALWAYS);

        VBox areaChartBox = createSavingsAreaChart();
        HBox.setHgrow(areaChartBox, Priority.ALWAYS);
        
        bottomRow.getChildren().addAll(lineChartBox, areaChartBox);

        section.getChildren().addAll(title, topRow, bottomRow);
        return section;
    }

    
    private VBox createEnhancedSpendingPieChart() {
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
            List<Map.Entry<String, Double>> sortedCategories = categorySpending.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toList());
            
            double total = categorySpending.values().stream().mapToDouble(Double::doubleValue).sum();
            
            for (Map.Entry<String, Double> entry : sortedCategories) {
                double percentage = (entry.getValue() / total) * 100;
                PieChart.Data data = new PieChart.Data(
                    entry.getKey() + String.format(" %.1f%%", percentage),
                    entry.getValue()
                );
                pieChart.getData().add(data);
            }

            VBox legend = createEnhancedLegend(sortedCategories, total);
            box.getChildren().addAll(title, pieChart, legend);
        }

        return box;
    }

    private VBox createCategoryBarChart() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(25));
        box.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 15;");
        box.setPrefWidth(600);

        Text title = new Text("Monthly Category Comparison");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: white;");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount (₱)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("");
        barChart.setLegendVisible(true);
        barChart.setPrefHeight(350);
        barChart.setStyle("-fx-background-color: transparent;");

        String userId = authService.getCurrentUser().getId();
        Map<String, Double> currentMonthSpending = dataService.getCurrentMonthSpending(userId);
        Map<String, Double> previousMonthSpending = getPreviousMonthSpending(userId);

        XYChart.Series<String, Number> currentSeries = new XYChart.Series<>();
        currentSeries.setName("Current Month");
        
        XYChart.Series<String, Number> previousSeries = new XYChart.Series<>();
        previousSeries.setName("Previous Month");

        Set<String> allCategories = new HashSet<>();
        allCategories.addAll(currentMonthSpending.keySet());
        allCategories.addAll(previousMonthSpending.keySet());

        for (String category : allCategories) {
            currentSeries.getData().add(new XYChart.Data<>(category, 
                currentMonthSpending.getOrDefault(category, 0.0)));
            previousSeries.getData().add(new XYChart.Data<>(category, 
                previousMonthSpending.getOrDefault(category, 0.0)));
        }

        barChart.getData().addAll(currentSeries, previousSeries);
        box.getChildren().addAll(title, barChart);

        return box;
    }

    private VBox createEnhancedBalanceTrendChart() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(25));
        box.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 15;");
        box.setPrefWidth(600);

        Text title = new Text("Balance & Expenses Trend");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: white;");

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
        yAxis.setLabel("Amount (₱)");

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
        balanceSeries.setName("Balance");

        XYChart.Series<Number, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");

        XYChart.Series<Number, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses");

        if (monthlyIncome.isEmpty() && monthlyExpenses.isEmpty()) {
            for (int i = 1; i <= 6; i++) {
                balanceSeries.getData().add(new XYChart.Data<>(i, 0));
                incomeSeries.getData().add(new XYChart.Data<>(i, 0));
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
                incomeSeries.getData().add(new XYChart.Data<>(month.getMonthValue(), income));
                expenseSeries.getData().add(new XYChart.Data<>(month.getMonthValue(), expense));
            }
        }

        lineChart.getData().addAll(incomeSeries, expenseSeries, balanceSeries);

        VBox stats = createEnhancedStats(monthlyIncome, monthlyExpenses);
        box.getChildren().addAll(title, lineChart, stats);
        return box;
    }

    private VBox createSavingsAreaChart() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(25));
        box.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 15;");
        box.setPrefWidth(600);

        Text title = new Text("Savings Accumulation (Area Chart)");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: white;");

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Month");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Savings (₱)");

        AreaChart<Number, Number> areaChart = new AreaChart<>(xAxis, yAxis);
        areaChart.setTitle("");
        areaChart.setLegendVisible(true);
        areaChart.setPrefHeight(350);
        areaChart.setStyle("-fx-background-color: transparent;");

        String userId = authService.getCurrentUser().getId();
        Map<YearMonth, Double> monthlyIncome = dataService.getMonthlyIncome(userId);
        Map<YearMonth, Double> monthlyExpenses = dataService.getMonthlyExpenses(userId);

        XYChart.Series<Number, Number> savingsSeries = new XYChart.Series<>();
        savingsSeries.setName("Cumulative Savings");

        if (monthlyIncome.isEmpty() && monthlyExpenses.isEmpty()) {
            for (int i = 1; i <= 6; i++) {
                savingsSeries.getData().add(new XYChart.Data<>(i, 0));
            }
        } else {
            TreeMap<YearMonth, Double> sortedIncome = new TreeMap<>(monthlyIncome);
            TreeMap<YearMonth, Double> sortedExpenses = new TreeMap<>(monthlyExpenses);

            TreeMap<YearMonth, Double> allMonths = new TreeMap<>();
            sortedIncome.forEach((month, amount) -> allMonths.put(month, amount));
            sortedExpenses.forEach((month, amount) -> allMonths.putIfAbsent(month, 0.0));

            double cumulativeSavings = 0;
            for (Map.Entry<YearMonth, Double> entry : allMonths.entrySet()) {
                YearMonth month = entry.getKey();
                double income = sortedIncome.getOrDefault(month, 0.0);
                double expense = sortedExpenses.getOrDefault(month, 0.0);
                cumulativeSavings += (income - expense);

                savingsSeries.getData().add(new XYChart.Data<>(month.getMonthValue(), cumulativeSavings));
            }
        }

        areaChart.getData().add(savingsSeries);
        box.getChildren().addAll(title, areaChart);

        return box;
    }


    
    private VBox createEnhancedFinancialInsights() {
        VBox section = new VBox(20);
        section.setMaxWidth(1400);
        section.setPadding(new Insets(25));
        section.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 15;");

        Text sectionTitle = new Text("Financial Insights");
        sectionTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: white;");

        String userId = authService.getCurrentUser().getId();
        Map<String, Double> categorySpending = dataService.getCategorySpending(userId);
        FinancialMetrics metrics = dataService.calculateMetricsOptimized(userId);

        GridPane insights = new GridPane();
        insights.setHgap(40);
        insights.setVgap(20);
        insights.setAlignment(Pos.CENTER);

        VBox highestExpense = createInsightCard(
            "Highest Expense Category",
            getHighestCategory(categorySpending),
            String.format("₱%.2f", getHighestAmount(categorySpending)),
            "#06ffa5"
        );

        VBox growthRate = createInsightCard(
            "Growth Rate Analysis",
            "Balance Trend",
            calculateGrowthRate(userId),
            "#06ffa5"
        );

        VBox budgetOptimization = createInsightCard(
            "Budget Optimization",
            "Recommendation",
            getBudgetOptimizationStatus(userId),
            "#f4a261"
        );

        VBox spendingPattern = createInsightCard(
            "Spending Pattern",
            "Analysis",
            getSpendingPatternInsight(userId),
            "#9775fa"
        );

        insights.add(highestExpense, 0, 0);
        insights.add(growthRate, 1, 0);
        insights.add(budgetOptimization, 0, 1);
        insights.add(spendingPattern, 1, 1);

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
    
    
    private VBox createEnhancedLegend(List<Map.Entry<String, Double>> sortedCategories, double total) {
        VBox legend = new VBox(10);
        legend.setPadding(new Insets(15, 0, 0, 0));

        for (Map.Entry<String, Double> entry : sortedCategories) {
            HBox legendItem = new HBox(10);
            legendItem.setAlignment(Pos.CENTER_LEFT);

            Label colorBox = new Label("●");
            colorBox.setStyle("-fx-text-fill: " + getCategoryColor(entry.getKey()) + "; -fx-font-size: 16px;");

            Label categoryLabel = new Label(entry.getKey());
            categoryLabel.setStyle("-fx-text-fill: #a8dadc; -fx-font-size: 13px;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            double percentage = (entry.getValue() / total) * 100;
            Label amountLabel = new Label(String.format("₱%.2f (%.1f%%)", entry.getValue(), percentage));
            amountLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");

            legendItem.getChildren().addAll(colorBox, categoryLabel, spacer, amountLabel);
            legend.getChildren().add(legendItem);
        }

        return legend;
    }
    
    private VBox createEnhancedStats(Map<YearMonth, Double> monthlyIncome, Map<YearMonth, Double> monthlyExpenses) {
        HBox stats = new HBox(40);
        stats.setAlignment(Pos.CENTER);
        stats.setPadding(new Insets(15, 0, 0, 0));

        FinancialMetrics metrics = dataService.calculateMetricsOptimized(authService.getCurrentUser().getId());
        int monthCount = Math.max(monthlyIncome.size(), monthlyExpenses.size());
        monthCount = monthCount > 0 ? monthCount : 1;

        VBox avgBalance = new VBox(5);
        avgBalance.setAlignment(Pos.CENTER);
        Label avgBalanceTitle = new Label("Average Monthly Balance");
        avgBalanceTitle.setStyle("-fx-text-fill: #a8dadc; -fx-font-size: 12px;");
        Label avgBalanceValue = new Label(String.format("₱%.2f", metrics.getCurrentBalance() / monthCount));
        avgBalanceValue.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        avgBalance.getChildren().addAll(avgBalanceTitle, avgBalanceValue);

        VBox avgExpenses = new VBox(5);
        avgExpenses.setAlignment(Pos.CENTER);
        Label avgExpensesTitle = new Label("Average Monthly Expenses");
        avgExpensesTitle.setStyle("-fx-text-fill: #a8dadc; -fx-font-size: 12px;");
        Label avgExpensesValue = new Label(String.format("₱%.2f", metrics.getTotalExpenses() / monthCount));
        avgExpensesValue.setStyle("-fx-text-fill: #e63946; -fx-font-size: 18px; -fx-font-weight: bold;");
        avgExpenses.getChildren().addAll(avgExpensesTitle, avgExpensesValue);
        
        VBox savingsRate = new VBox(5);
        savingsRate.setAlignment(Pos.CENTER);
        Label savingsRateTitle = new Label("Savings Rate");
        savingsRateTitle.setStyle("-fx-text-fill: #a8dadc; -fx-font-size: 12px;");
        Label savingsRateValue = new Label(metrics.getFormattedSavingsRate());
        savingsRateValue.setStyle("-fx-text-fill: #06ffa5; -fx-font-size: 18px; -fx-font-weight: bold;");
        savingsRate.getChildren().addAll(savingsRateTitle, savingsRateValue);

        stats.getChildren().addAll(avgBalance, avgExpenses, savingsRate);
        
        VBox container = new VBox(10);
        container.getChildren().add(stats);
        return container;
    }
    
    private Map<String, Double> getPreviousMonthSpending(String userId) {
        return new HashMap<>();
    }
    
    private String calculateGrowthRate(String userId) {
        Map<YearMonth, Double> monthlyIncome = dataService.getMonthlyIncome(userId);
        Map<YearMonth, Double> monthlyExpenses = dataService.getMonthlyExpenses(userId);
        
        if (monthlyIncome.size() < 2) return "Insufficient Data";
        
        List<Double> monthlyBalances = new ArrayList<>();
        TreeMap<YearMonth, Double> sortedIncome = new TreeMap<>(monthlyIncome);
        TreeMap<YearMonth, Double> sortedExpenses = new TreeMap<>(monthlyExpenses);
        
        double runningBalance = 0;
        for (Map.Entry<YearMonth, Double> entry : sortedIncome.entrySet()) {
            YearMonth month = entry.getKey();
            double income = sortedIncome.getOrDefault(month, 0.0);
            double expense = sortedExpenses.getOrDefault(month, 0.0);
            runningBalance += (income - expense);
            monthlyBalances.add(runningBalance);
        }
        
        if (monthlyBalances.size() < 2) return "Insufficient Data";
        
        double firstMonth = monthlyBalances.get(0);
        double lastMonth = monthlyBalances.get(monthlyBalances.size() - 1);
        double growthRate = ((lastMonth - firstMonth) / Math.abs(firstMonth)) * 100;
        
        return String.format("%.1f%%", growthRate);
    }
    
    private String getBudgetOptimizationStatus(String userId) {
        List<Transaction> transactions = dataService.getTransactionsForUser(userId);
        Map<String, Double> optimalBudget = dataService.optimalBudgetAllocation(transactions, 5000.0);
        
        if (optimalBudget.isEmpty()) return "No Data";
        
        return optimalBudget.size() + " categories optimized";
    }
    
    private String getSpendingPatternInsight(String userId) {
        List<Transaction> transactions = dataService.getTransactionsForUser(userId);
        Map<String, List<String>> spendingGraph = dataService.buildSpendingGraph(transactions);
        
        if (spendingGraph.isEmpty()) return "No Patterns";
        
        return spendingGraph.size() + " connected patterns";
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
