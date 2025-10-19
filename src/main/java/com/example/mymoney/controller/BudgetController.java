package com.example.mymoney.controller;

import com.example.mymoney.model.Budget;
import com.example.mymoney.service.AuthenticationService;
import com.example.mymoney.service.BudgetService;
import com.example.mymoney.service.DataService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.Map;

public class BudgetController {
    private final AuthenticationService authService;
    private final DataService dataService;
    private final BudgetService budgetService;
    private TextField overallBudgetField;
    private Label totalSpentLabel;
    private ProgressBar overallProgressBar;
    private GridPane categoryGrid;

    public BudgetController() {
        this.authService = AuthenticationService.getInstance();
        this.dataService = DataService.getInstance();
        this.budgetService = BudgetService.getInstance();
    }

    public ScrollPane createBudgetView() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_CENTER);

        Text title = new Text("Budget Management");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-fill: white;");

        // Overall budget section
        VBox overallSection = createOverallBudgetSection();

        // Category budgets
        VBox categorySection = createCategoryBudgetsSection();

        content.getChildren().addAll(title, overallSection, categorySection);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        refresh();

        return scrollPane;
    }

    private VBox createOverallBudgetSection() {
        VBox section = new VBox(15);
        section.setMaxWidth(1200);
        section.setPadding(new Insets(25));
        section.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 15;");

        Text sectionTitle = new Text("Overall Monthly Budget");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: white;");

        HBox budgetInput = new HBox(15);
        budgetInput.setAlignment(Pos.CENTER_LEFT);

        overallBudgetField = new TextField();
        overallBudgetField.setPromptText("3500");
        overallBudgetField.getStyleClass().add("text-field");
        overallBudgetField.setPrefWidth(400);

        Button updateButton = new Button("Update");
        updateButton.getStyleClass().add("primary-button");
        updateButton.setOnAction(e -> updateOverallBudget());

        budgetInput.getChildren().addAll(overallBudgetField, updateButton);

        // Total spent this month
        VBox spentSection = new VBox(10);
        spentSection.setPadding(new Insets(15, 0, 0, 0));

        HBox spentHeader = new HBox();
        spentHeader.setAlignment(Pos.CENTER_LEFT);

        Label spentLabel = new Label("Total Spent This Month");
        spentLabel.setStyle("-fx-text-fill: #a8dadc; -fx-font-size: 14px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        totalSpentLabel = new Label("$0 / $0");
        totalSpentLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        spentHeader.getChildren().addAll(spentLabel, spacer, totalSpentLabel);

        overallProgressBar = new ProgressBar(0);
        overallProgressBar.setMaxWidth(Double.MAX_VALUE);
        overallProgressBar.setPrefHeight(15);

        Label percentLabel = new Label("0% of budget used");
        percentLabel.setStyle("-fx-text-fill: #a8dadc; -fx-font-size: 12px;");
        percentLabel.setId("overallPercentLabel");

        spentSection.getChildren().addAll(spentHeader, overallProgressBar, percentLabel);

        section.getChildren().addAll(sectionTitle, budgetInput, spentSection);
        return section;
    }

    private VBox createCategoryBudgetsSection() {
        VBox section = new VBox(20);
        section.setMaxWidth(1200);

        Text sectionTitle = new Text("Category Budgets");
        sectionTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: white;");

        categoryGrid = new GridPane();
        categoryGrid.setHgap(20);
        categoryGrid.setVgap(20);

        // Configure grid columns
        for (int i = 0; i < 2; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(50);
            col.setHgrow(Priority.ALWAYS);
            categoryGrid.getColumnConstraints().add(col);
        }

        section.getChildren().addAll(sectionTitle, categoryGrid);
        return section;
    }

    private VBox createCategoryBudgetCard(String category, double budget, double spent) {
        VBox card = new VBox(12);
        card.getStyleClass().add("metric-card");
        card.setPadding(new Insets(20));

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label categoryLabel = new Label(category);
        categoryLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        double percentage = budget > 0 ? (spent / budget) * 100 : 0;
        Label percentLabel = new Label(String.format("%.0f%%", percentage));
        
        String percentColor = percentage >= 90 ? "#e63946" : (percentage >= 70 ? "#f4a261" : "#06ffa5");
        percentLabel.setStyle("-fx-text-fill: " + percentColor + "; -fx-font-size: 14px; -fx-font-weight: bold;");

        header.getChildren().addAll(categoryLabel, spacer1, percentLabel);

        // Amount
        Label amountLabel = new Label(String.format("$%.0f / $%.0f", spent, budget));
        amountLabel.setStyle("-fx-text-fill: #a8dadc; -fx-font-size: 14px;");

        // Progress bar
        ProgressBar progressBar = new ProgressBar(budget > 0 ? Math.min(spent / budget, 1.0) : 0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(10);
        
        String barColor = percentage >= 90 ? "#e63946" : (percentage >= 70 ? "#f4a261" : "#06ffa5");
        progressBar.setStyle(
            "-fx-accent: " + barColor + ";" +
            "-fx-control-inner-background: rgba(255, 255, 255, 0.1);"
        );

        // Budget input
        HBox budgetInput = new HBox(10);
        budgetInput.setAlignment(Pos.CENTER_LEFT);

        TextField budgetField = new TextField(String.valueOf((int)budget));
        budgetField.getStyleClass().add("text-field");
        budgetField.setPrefWidth(150);
        HBox.setHgrow(budgetField, Priority.ALWAYS);

        Button setButton = new Button("Set");
        setButton.getStyleClass().add("primary-button");
        setButton.setOnAction(e -> updateCategoryBudget(category, budgetField));

        budgetInput.getChildren().addAll(budgetField, setButton);

        card.getChildren().addAll(header, amountLabel, progressBar, budgetInput);
        return card;
    }

    private void updateOverallBudget() {
        try {
            double amount = Double.parseDouble(overallBudgetField.getText().trim());
            if (amount <= 0) {
                showError("Budget must be positive");
                return;
            }

            String userId = authService.getCurrentUser().getId();
            budgetService.updateOverallBudget(userId, amount);
            refresh();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Overall budget updated successfully!");
            alert.showAndWait();
        } catch (NumberFormatException e) {
            showError("Invalid budget amount");
        }
    }

    private void updateCategoryBudget(String category, TextField field) {
        try {
            double amount = Double.parseDouble(field.getText().trim());
            if (amount <= 0) {
                showError("Budget must be positive");
                return;
            }

            String userId = authService.getCurrentUser().getId();
            budgetService.updateCategoryBudget(userId, category, amount);
            refresh();
        } catch (NumberFormatException e) {
            showError("Invalid budget amount");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void refresh() {
        String userId = authService.getCurrentUser().getId();
        Budget budget = budgetService.getBudget(userId);
        Map<String, Double> currentMonthSpending = dataService.getCurrentMonthSpending(userId);
        double totalSpent = dataService.getCurrentMonthTotalExpenses(userId);

        // Update overall budget
        overallBudgetField.setText(String.format("%.0f", budget.getOverallMonthlyBudget()));
        totalSpentLabel.setText(String.format("$%.0f / $%.0f", totalSpent, budget.getOverallMonthlyBudget()));
        
        double overallPercent = budget.getOverallMonthlyBudget() > 0 
            ? (totalSpent / budget.getOverallMonthlyBudget()) 
            : 0;
        overallProgressBar.setProgress(Math.min(overallPercent, 1.0));
        
        String overallColor = overallPercent >= 0.9 ? "#e63946" : (overallPercent >= 0.7 ? "#f4a261" : "#06ffa5");
        overallProgressBar.setStyle(
            "-fx-accent: " + overallColor + ";" +
            "-fx-control-inner-background: rgba(255, 255, 255, 0.1);"
        );

        Label percentLabel = (Label) overallProgressBar.getParent().lookup("#overallPercentLabel");
        if (percentLabel != null) {
            percentLabel.setText(String.format("%.1f%% of budget used", overallPercent * 100));
        }

        // Update category budgets
        categoryGrid.getChildren().clear();
        String[] categories = {"Groceries", "Utilities", "Transportation", "Entertainment", "Healthcare", "Shopping"};
        
        int row = 0, col = 0;
        for (String category : categories) {
            double categoryBudget = budget.getCategoryBudget(category);
            double categorySpent = currentMonthSpending.getOrDefault(category, 0.0);
            
            VBox card = createCategoryBudgetCard(category, categoryBudget, categorySpent);
            categoryGrid.add(card, col, row);
            
            col++;
            if (col >= 2) {
                col = 0;
                row++;
            }
        }
    }
}
