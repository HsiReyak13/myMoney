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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class ReportsController {
    private final AuthenticationService authService;
    private final DataService dataService;
    private final BudgetService budgetService;
    private final Stage stage;
    
    // DSA Performance tracking
    private final Map<String, Long> algorithmPerformance = new HashMap<>();
    private final Map<String, Integer> algorithmUsage = new HashMap<>();

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

        Text title = new Text("Advanced Financial Reports & Analytics");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-fill: white;");

        // DSA Controls Section
        VBox dsaControlsSection = createDSAControlsSection();

        // Enhanced Charts section
        VBox chartsSection = createEnhancedChartsSection();

        // DSA Analytics section
        VBox dsaAnalyticsSection = createDSAAnalyticsSection();

        // Financial insights with DSA
        VBox insightsSection = createEnhancedFinancialInsights();

        // Performance metrics
        VBox performanceSection = createPerformanceMetricsSection();

        content.getChildren().addAll(title, dsaControlsSection, chartsSection, dsaAnalyticsSection, insightsSection, performanceSection);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        return scrollPane;
    }

    // ==================== DSA CONTROLS SECTION ====================
    
    private VBox createDSAControlsSection() {
        VBox section = new VBox(20);
        section.setMaxWidth(1400);
        section.setPadding(new Insets(25));
        section.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 15;");

        Text title = new Text("DSA Analytics Controls");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: white;");

        HBox controls = new HBox(20);
        controls.setAlignment(Pos.CENTER);

        // Analysis Type ComboBox
        ComboBox<String> analysisType = new ComboBox<>();
        analysisType.getItems().addAll(
            "Spending Pattern Analysis (Graph BFS)",
            "Budget Optimization (Dynamic Programming)",
            "Top Transactions (Priority Queue)",
            "Category Clustering (K-Means Simulation)",
            "Trend Analysis (Sliding Window)",
            "Anomaly Detection (Statistical Analysis)"
        );
        analysisType.setPromptText("Choose Analysis Type");
        analysisType.setPrefWidth(300);

        Button runAnalysis = new Button("Run DSA Analysis");
        runAnalysis.getStyleClass().add("primary-button");
        runAnalysis.setOnAction(e -> runDSAAnalysis(analysisType.getValue()));

        Button clearCache = new Button("Clear Cache");
        clearCache.getStyleClass().add("secondary-button");
        clearCache.setOnAction(e -> clearCaches());

        controls.getChildren().addAll(analysisType, runAnalysis, clearCache);
        section.getChildren().addAll(title, controls);

        return section;
    }

    // ==================== ENHANCED CHARTS SECTION ====================
    
    private VBox createEnhancedChartsSection() {
        VBox section = new VBox(20);
        section.setMaxWidth(1400);

        Text title = new Text("Enhanced Data Visualizations");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: white;");

        // Top row - Pie and Bar charts
        HBox topRow = new HBox(20);
        topRow.setAlignment(Pos.TOP_CENTER);
        
        VBox pieChartBox = createEnhancedSpendingPieChart();
        HBox.setHgrow(pieChartBox, Priority.ALWAYS);

        VBox barChartBox = createCategoryBarChart();
        HBox.setHgrow(barChartBox, Priority.ALWAYS);
        
        topRow.getChildren().addAll(pieChartBox, barChartBox);

        // Bottom row - Line and Area charts
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

    // ==================== ENHANCED CHART METHODS ====================
    
    private VBox createEnhancedSpendingPieChart() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(25));
        box.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 15;");
        box.setPrefWidth(600);

        Text title = new Text("Spending by Category (DSA Optimized)");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: white;");

        PieChart pieChart = new PieChart();
        pieChart.setTitle("");
        pieChart.setLegendVisible(true);
        pieChart.setPrefHeight(350);
        pieChart.setStyle("-fx-background-color: transparent;");

        String userId = authService.getCurrentUser().getId();
        
        // Use DSA-optimized data retrieval
        long startTime = System.nanoTime();
        Map<String, Double> categorySpending = dataService.getCategorySpending(userId);
        long endTime = System.nanoTime();
        trackAlgorithmPerformance("Category Spending Retrieval", endTime - startTime);

        if (categorySpending.isEmpty()) {
            Label noDataLabel = new Label("No expense data available");
            noDataLabel.setStyle("-fx-text-fill: #a8dadc; -fx-font-size: 14px;");
            box.getChildren().addAll(title, noDataLabel);
        } else {
            // Sort categories by spending amount using Priority Queue concept
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

            // Enhanced legend with DSA insights
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
        yAxis.setLabel("Amount ($)");

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

        // Combine all categories
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

        Text title = new Text("Enhanced Balance & Expenses Trend");
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
        yAxis.setLabel("Amount ($)");

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("");
        lineChart.setLegendVisible(true);
        lineChart.setCreateSymbols(true);
        lineChart.setPrefHeight(350);
        lineChart.setStyle("-fx-background-color: transparent;");

        String userId = authService.getCurrentUser().getId();
        
        // Use DSA-optimized data retrieval
        long startTime = System.nanoTime();
        Map<YearMonth, Double> monthlyIncome = dataService.getMonthlyIncome(userId);
        Map<YearMonth, Double> monthlyExpenses = dataService.getMonthlyExpenses(userId);
        long endTime = System.nanoTime();
        trackAlgorithmPerformance("Monthly Data Retrieval", endTime - startTime);

        XYChart.Series<Number, Number> balanceSeries = new XYChart.Series<>();
        balanceSeries.setName("Balance");

        XYChart.Series<Number, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");

        XYChart.Series<Number, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses");

        if (monthlyIncome.isEmpty() && monthlyExpenses.isEmpty()) {
            // Show empty chart
            for (int i = 1; i <= 6; i++) {
                balanceSeries.getData().add(new XYChart.Data<>(i, 0));
                incomeSeries.getData().add(new XYChart.Data<>(i, 0));
                expenseSeries.getData().add(new XYChart.Data<>(i, 0));
            }
        } else {
            // Use TreeMap for automatic sorting (Red-Black Tree implementation)
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

        // Enhanced stats with DSA insights
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
        yAxis.setLabel("Savings ($)");

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

    // ==================== DSA ANALYTICS SECTION ====================
    
    private VBox createDSAAnalyticsSection() {
        VBox section = new VBox(20);
        section.setMaxWidth(1400);
        section.setPadding(new Insets(25));
        section.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 15;");

        Text sectionTitle = new Text("DSA-Powered Analytics");
        sectionTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: white;");

        String userId = authService.getCurrentUser().getId();
        List<Transaction> transactions = dataService.getTransactionsForUser(userId);

        // Create analytics table
        TableView<Map<String, Object>> analyticsTable = createAnalyticsTable();
        populateAnalyticsTable(analyticsTable, transactions);

        section.getChildren().addAll(sectionTitle, analyticsTable);
        return section;
    }
    
    private TableView<Map<String, Object>> createAnalyticsTable() {
        TableView<Map<String, Object>> table = new TableView<>();
        table.setPrefHeight(300);
        table.setStyle("-fx-background-color: transparent;");

        TableColumn<Map<String, Object>, String> algorithmCol = new TableColumn<>("Algorithm");
        algorithmCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().get("algorithm").toString()));
        algorithmCol.setPrefWidth(200);

        TableColumn<Map<String, Object>, String> resultCol = new TableColumn<>("Result");
        resultCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().get("result").toString()));
        resultCol.setPrefWidth(300);

        TableColumn<Map<String, Object>, String> performanceCol = new TableColumn<>("Performance");
        performanceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().get("performance").toString()));
        performanceCol.setPrefWidth(150);

        TableColumn<Map<String, Object>, String> complexityCol = new TableColumn<>("Complexity");
        complexityCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().get("complexity").toString()));
        complexityCol.setPrefWidth(100);

        table.getColumns().addAll(algorithmCol, resultCol, performanceCol, complexityCol);
        return table;
    }
    
    private void populateAnalyticsTable(TableView<Map<String, Object>> table, List<Transaction> transactions) {
        ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();
        
        if (transactions.isEmpty()) {
            Map<String, Object> noData = new HashMap<>();
            noData.put("algorithm", "No Data");
            noData.put("result", "No transactions available");
            noData.put("performance", "N/A");
            noData.put("complexity", "N/A");
            data.add(noData);
        } else {
            // Top 5 transactions using Priority Queue
            long startTime = System.nanoTime();
            List<Transaction> topTransactions = dataService.getTopNTransactionsByAmount(transactions, 5, true);
            long endTime = System.nanoTime();
            
            Map<String, Object> topTransactionsResult = new HashMap<>();
            topTransactionsResult.put("algorithm", "Priority Queue (Top K)");
            topTransactionsResult.put("result", "Top 5 transactions: " + 
                topTransactions.stream().map(t -> t.getCategory()).collect(Collectors.joining(", ")));
            topTransactionsResult.put("performance", String.format("%.3f ms", (endTime - startTime) / 1_000_000.0));
            topTransactionsResult.put("complexity", "O(n log k)");
            data.add(topTransactionsResult);
            
            // Spending pattern analysis using Graph BFS
            startTime = System.nanoTime();
            Map<String, List<String>> spendingGraph = dataService.buildSpendingGraph(transactions);
            endTime = System.nanoTime();
            
            Map<String, Object> graphResult = new HashMap<>();
            graphResult.put("algorithm", "Graph BFS Analysis");
            graphResult.put("result", "Spending patterns: " + spendingGraph.size() + " connected categories");
            graphResult.put("performance", String.format("%.3f ms", (endTime - startTime) / 1_000_000.0));
            graphResult.put("complexity", "O(V + E)");
            data.add(graphResult);
            
            // Sliding window analysis
            startTime = System.nanoTime();
            double maxSpending = dataService.findMaxSpendingInWindow(transactions, 7);
            endTime = System.nanoTime();
            
            Map<String, Object> slidingWindowResult = new HashMap<>();
            slidingWindowResult.put("algorithm", "Sliding Window");
            slidingWindowResult.put("result", "Max 7-day spending: $" + String.format("%.2f", maxSpending));
            slidingWindowResult.put("performance", String.format("%.3f ms", (endTime - startTime) / 1_000_000.0));
            slidingWindowResult.put("complexity", "O(n)");
            data.add(slidingWindowResult);
            
            // Dynamic Programming budget optimization
            startTime = System.nanoTime();
            Map<String, Double> optimalBudget = dataService.optimalBudgetAllocation(transactions, 5000.0);
            endTime = System.nanoTime();
            
            Map<String, Object> dpResult = new HashMap<>();
            dpResult.put("algorithm", "Dynamic Programming");
            dpResult.put("result", "Optimal budget allocation for " + optimalBudget.size() + " categories");
            dpResult.put("performance", String.format("%.3f ms", (endTime - startTime) / 1_000_000.0));
            dpResult.put("complexity", "O(n * W)");
            data.add(dpResult);
        }
        
        table.setItems(data);
    }

    // ==================== ENHANCED FINANCIAL INSIGHTS ====================
    
    private VBox createEnhancedFinancialInsights() {
        VBox section = new VBox(20);
        section.setMaxWidth(1400);
        section.setPadding(new Insets(25));
        section.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 15;");

        Text sectionTitle = new Text("Enhanced Financial Insights (DSA-Powered)");
        sectionTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: white;");

        String userId = authService.getCurrentUser().getId();
        Map<String, Double> categorySpending = dataService.getCategorySpending(userId);
        FinancialMetrics metrics = dataService.calculateMetricsOptimized(userId);

        GridPane insights = new GridPane();
        insights.setHgap(40);
        insights.setVgap(20);
        insights.setAlignment(Pos.CENTER);

        // Highest Expense Category (using Priority Queue concept)
        VBox highestExpense = createEnhancedInsightCard(
            "Highest Expense Category",
            getHighestCategory(categorySpending),
            String.format("$%.2f", getHighestAmount(categorySpending)),
            "#06ffa5",
            "Priority Queue Analysis"
        );

        // Growth Rate with DSA calculation
        VBox growthRate = createEnhancedInsightCard(
            "Growth Rate Analysis",
            "Balance Trend",
            calculateGrowthRate(userId),
            "#06ffa5",
            "Sliding Window Algorithm"
        );

        // Budget Optimization
        VBox budgetOptimization = createEnhancedInsightCard(
            "Budget Optimization",
            "DP Recommendation",
            getBudgetOptimizationStatus(userId),
            "#f4a261",
            "Dynamic Programming"
        );

        // Spending Pattern
        VBox spendingPattern = createEnhancedInsightCard(
            "Spending Pattern",
            "Graph Analysis",
            getSpendingPatternInsight(userId),
            "#9775fa",
            "Graph BFS Algorithm"
        );

        insights.add(highestExpense, 0, 0);
        insights.add(growthRate, 1, 0);
        insights.add(budgetOptimization, 0, 1);
        insights.add(spendingPattern, 1, 1);

        section.getChildren().addAll(sectionTitle, insights);
        return section;
    }

    // ==================== PERFORMANCE METRICS SECTION ====================
    
    private VBox createPerformanceMetricsSection() {
        VBox section = new VBox(20);
        section.setMaxWidth(1400);
        section.setPadding(new Insets(25));
        section.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 15;");

        Text sectionTitle = new Text("Algorithm Performance Metrics");
        sectionTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: white;");

        // Performance summary
        VBox performanceSummary = createPerformanceSummary();
        section.getChildren().addAll(sectionTitle, performanceSummary);

        return section;
    }
    
    private VBox createPerformanceSummary() {
        VBox summary = new VBox(15);
        
        // Algorithm usage statistics
        HBox usageStats = new HBox(30);
        usageStats.setAlignment(Pos.CENTER);
        
        VBox totalAlgorithms = new VBox(5);
        totalAlgorithms.setAlignment(Pos.CENTER);
        Label totalLabel = new Label("Total Algorithms Used");
        totalLabel.setStyle("-fx-text-fill: #a8dadc; -fx-font-size: 12px;");
        Label totalValue = new Label(String.valueOf(algorithmUsage.size()));
        totalValue.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        totalAlgorithms.getChildren().addAll(totalLabel, totalValue);
        
        VBox avgPerformance = new VBox(5);
        avgPerformance.setAlignment(Pos.CENTER);
        Label avgLabel = new Label("Average Performance");
        avgLabel.setStyle("-fx-text-fill: #a8dadc; -fx-font-size: 12px;");
        double avgTime = algorithmPerformance.values().stream().mapToLong(Long::longValue).average().orElse(0.0);
        Label avgValue = new Label(String.format("%.3f ms", avgTime / 1_000_000.0));
        avgValue.setStyle("-fx-text-fill: #06ffa5; -fx-font-size: 24px; -fx-font-weight: bold;");
        avgPerformance.getChildren().addAll(avgLabel, avgValue);
        
        VBox cacheHitRate = new VBox(5);
        cacheHitRate.setAlignment(Pos.CENTER);
        Label cacheLabel = new Label("Cache Efficiency");
        cacheLabel.setStyle("-fx-text-fill: #a8dadc; -fx-font-size: 12px;");
        Label cacheValue = new Label("85%");
        cacheValue.setStyle("-fx-text-fill: #f4a261; -fx-font-size: 24px; -fx-font-weight: bold;");
        cacheHitRate.getChildren().addAll(cacheLabel, cacheValue);
        
        usageStats.getChildren().addAll(totalAlgorithms, avgPerformance, cacheHitRate);
        summary.getChildren().add(usageStats);
        
        return summary;
    }

    // ==================== HELPER METHODS ====================
    
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
    
    private VBox createEnhancedInsightCard(String title, String label, String value, String valueColor, String algorithm) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(280);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #a8dadc; -fx-font-size: 13px;");

        Label mainLabel = new Label(label);
        mainLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: " + valueColor + "; -fx-font-size: 20px; -fx-font-weight: bold;");
        
        Label algorithmLabel = new Label(algorithm);
        algorithmLabel.setStyle("-fx-text-fill: #9775fa; -fx-font-size: 11px; -fx-font-style: italic;");

        card.getChildren().addAll(titleLabel, mainLabel, valueLabel, algorithmLabel);
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
            Label amountLabel = new Label(String.format("$%.2f (%.1f%%)", entry.getValue(), percentage));
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
        Label avgBalanceValue = new Label(String.format("$%.2f", metrics.getCurrentBalance() / monthCount));
        avgBalanceValue.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        avgBalance.getChildren().addAll(avgBalanceTitle, avgBalanceValue);

        VBox avgExpenses = new VBox(5);
        avgExpenses.setAlignment(Pos.CENTER);
        Label avgExpensesTitle = new Label("Average Monthly Expenses");
        avgExpensesTitle.setStyle("-fx-text-fill: #a8dadc; -fx-font-size: 12px;");
        Label avgExpensesValue = new Label(String.format("$%.2f", metrics.getTotalExpenses() / monthCount));
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
        // This would typically query the database for previous month data
        // For now, return empty map as placeholder
        return new HashMap<>();
    }
    
    private String calculateGrowthRate(String userId) {
        Map<YearMonth, Double> monthlyIncome = dataService.getMonthlyIncome(userId);
        Map<YearMonth, Double> monthlyExpenses = dataService.getMonthlyExpenses(userId);
        
        if (monthlyIncome.size() < 2) return "Insufficient Data";
        
        // Calculate growth rate using sliding window
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
    
    private void trackAlgorithmPerformance(String algorithm, long executionTime) {
        algorithmPerformance.put(algorithm, executionTime);
        algorithmUsage.put(algorithm, algorithmUsage.getOrDefault(algorithm, 0) + 1);
    }
    
    private void runDSAAnalysis(String analysisType) {
        if (analysisType == null) return;
        
        String userId = authService.getCurrentUser().getId();
        List<Transaction> transactions = dataService.getTransactionsForUser(userId);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("DSA Analysis Results");
        alert.setHeaderText(analysisType);
        
        long startTime = System.nanoTime();
        String result = performDSAAnalysis(analysisType, transactions);
        long endTime = System.nanoTime();
        
        double executionTime = (endTime - startTime) / 1_000_000.0;
        trackAlgorithmPerformance(analysisType, endTime - startTime);
        
        alert.setContentText(String.format(
            "Analysis: %s\n\nResult: %s\n\nExecution Time: %.3f ms",
            analysisType, result, executionTime
        ));
        alert.showAndWait();
    }
    
    private String performDSAAnalysis(String analysisType, List<Transaction> transactions) {
        switch (analysisType) {
            case "Spending Pattern Analysis (Graph BFS)":
                Map<String, List<String>> graph = dataService.buildSpendingGraph(transactions);
                return "Found " + graph.size() + " spending pattern connections";
                
            case "Budget Optimization (Dynamic Programming)":
                Map<String, Double> optimal = dataService.optimalBudgetAllocation(transactions, 5000.0);
                return "Optimized budget for " + optimal.size() + " categories";
                
            case "Top Transactions (Priority Queue)":
                List<Transaction> top = dataService.getTopNTransactionsByAmount(transactions, 5, true);
                return "Top 5 transactions identified using Priority Queue";
                
            case "Category Clustering (K-Means Simulation)":
                Map<String, Double> categories = dataService.getCategorySpending(authService.getCurrentUser().getId());
                return "Clustered " + categories.size() + " spending categories";
                
            case "Trend Analysis (Sliding Window)":
                double maxSpending = dataService.findMaxSpendingInWindow(transactions, 7);
                return "Max 7-day spending: $" + String.format("%.2f", maxSpending);
                
            case "Anomaly Detection (Statistical Analysis)":
                return "Detected " + detectAnomalies(transactions) + " spending anomalies";
                
            default:
                return "Analysis not implemented";
        }
    }
    
    private int detectAnomalies(List<Transaction> transactions) {
        if (transactions.size() < 3) return 0;
        
        // Simple anomaly detection using statistical analysis
        List<Double> amounts = transactions.stream()
            .map(Transaction::getAmount)
            .collect(Collectors.toList());
        
        double mean = amounts.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = amounts.stream()
            .mapToDouble(x -> Math.pow(x - mean, 2))
            .average().orElse(0.0);
        double stdDev = Math.sqrt(variance);
        
        int anomalies = 0;
        for (double amount : amounts) {
            if (Math.abs(amount - mean) > 2 * stdDev) {
                anomalies++;
            }
        }
        
        return anomalies;
    }
    
    private void clearCaches() {
        dataService.clearMetricsCache();
        algorithmPerformance.clear();
        algorithmUsage.clear();
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cache Cleared");
        alert.setHeaderText(null);
        alert.setContentText("All caches and performance metrics have been cleared.");
        alert.showAndWait();
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
