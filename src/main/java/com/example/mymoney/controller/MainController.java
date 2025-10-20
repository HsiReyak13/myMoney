package com.example.mymoney.controller;

import com.example.mymoney.service.AuthenticationService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainController {
    private final Stage stage;
    private final AuthenticationService authService;
    private DashboardController dashboardController;
    private BudgetController budgetController;
    private TransactionsController transactionsController;
    private ReportsController reportsController;
    private BorderPane contentArea;
    private Button selectedNavButton;
    private VBox sidebar;

    public MainController(Stage stage) {
        this.stage = stage;
        this.authService = AuthenticationService.getInstance();
    }

    public Scene createMainScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(0));

        HBox topBar = createTopBar();
        root.setTop(topBar);

        sidebar = createSidebar();
        root.setLeft(sidebar);

        contentArea = new BorderPane();
        root.setCenter(contentArea);

        initializeControllers();

        showDashboard();

        Scene scene = new Scene(root, 1400, 800);
        scene.getStylesheets().add(getClass().getResource("/com/example/mymoney/styles.css").toExternalForm());
        return scene;
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");

        Label welcomeLabel = new Label("👋 Welcome, " + authService.getCurrentUser().getUsername() + "!");
        welcomeLabel.setStyle("-fx-text-fill: #64B5F6; -fx-font-size: 18px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(100, 181, 246, 0.3), 2, 0, 0, 1);");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("danger-button");
        logoutButton.setOnAction(e -> logout());

        topBar.getChildren().addAll(welcomeLabel, spacer, logoutButton);
        return topBar;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(20, 10, 20, 10));
        sidebar.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2); -fx-border-color: rgba(255, 255, 255, 0.1); -fx-border-width: 0 1 0 0;");

        Label appTitle = new Label("💰 MyMoney");
        appTitle.setStyle(
            "-fx-text-fill: linear-gradient(from 0% 0% to 100% 100%, #FFD700, #FFA500); " +
            "-fx-font-size: 28px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 0 0 25 0; " +
            "-fx-effect: dropshadow(gaussian, rgba(255, 215, 0, 0.4), 3, 0, 0, 1);"
        );
        sidebar.getChildren().add(appTitle);

        Button dashboardBtn = createNavButton("Dashboard", "📊");
        Button transactionsBtn = createNavButton("Transactions", "💰");
        Button budgetBtn = createNavButton("Budget", "📈");
        Button reportsBtn = createNavButton("Reports", "📋");

        dashboardBtn.setOnAction(e -> showDashboard());
        transactionsBtn.setOnAction(e -> showTransactions());
        budgetBtn.setOnAction(e -> showBudget());
        reportsBtn.setOnAction(e -> showReports());

        sidebar.getChildren().addAll(dashboardBtn, transactionsBtn, budgetBtn, reportsBtn);

        return sidebar;
    }

    private Button createNavButton(String text, String icon) {
        Button button = new Button(icon + " " + text);
        button.setPrefWidth(180);
        button.setPrefHeight(50);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px; " +
            "-fx-padding: 10 15;"
        );

        button.setOnMouseEntered(e -> {
            if (button != selectedNavButton) {
                button.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.1); " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 14px; " +
                    "-fx-border-radius: 8px; " +
                    "-fx-background-radius: 8px; " +
                    "-fx-padding: 10 15;"
                );
            }
        });

        button.setOnMouseExited(e -> {
            if (button != selectedNavButton) {
                button.setStyle(
                    "-fx-background-color: transparent; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 14px; " +
                    "-fx-border-radius: 8px; " +
                    "-fx-background-radius: 8px; " +
                    "-fx-padding: 10 15;"
                );
            }
        });

        return button;
    }

    private void initializeControllers() {
        dashboardController = new DashboardController();
        budgetController = new BudgetController();
        transactionsController = new TransactionsController(stage, () -> {
            dashboardController.refresh();
            budgetController.refresh();
        });
        reportsController = new ReportsController(stage);
    }

    private void showDashboard() {
        setSelectedButton("Dashboard");
        contentArea.setCenter(dashboardController.createDashboardView());
    }

    private void showTransactions() {
        setSelectedButton("Transactions");
        contentArea.setCenter(transactionsController.createTransactionsView());
    }

    private void showBudget() {
        setSelectedButton("Budget");
        contentArea.setCenter(budgetController.createBudgetView());
    }

    private void showReports() {
        setSelectedButton("Reports");
        contentArea.setCenter(reportsController.createReportsView());
    }

    private void setSelectedButton(String buttonText) {
        for (var child : sidebar.getChildren()) {
            if (child instanceof Button) {
                Button btn = (Button) child;
                if (btn.getText().contains(buttonText)) {
                    selectedNavButton = btn;
                    btn.setStyle(
                        "-fx-background-color: rgba(0, 123, 255, 0.3); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-padding: 10 15; " +
                        "-fx-border-color: rgba(0, 123, 255, 0.6); " +
                        "-fx-border-width: 1px;"
                    );
                } else {
                    btn.setStyle(
                        "-fx-background-color: transparent; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-padding: 10 15;"
                    );
                }
            }
        }
    }

    private void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                authService.logout();
                WelcomeController welcomeController = new WelcomeController(stage);
                Scene welcomeScene = welcomeController.createWelcomeScene();
                stage.setScene(welcomeScene);
            }
        });
    }
}
