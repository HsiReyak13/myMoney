package com.example.mymoney.controller;

import com.example.mymoney.service.AuthenticationService;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainController {
    private final Stage stage;
    private final AuthenticationService authService;
    private DashboardController dashboardController;
    private BudgetController budgetController;

    public MainController(Stage stage) {
        this.stage = stage;
        this.authService = AuthenticationService.getInstance();
    }

    public Scene createMainScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(0));

        // Top bar with user info and logout
        HBox topBar = createTopBar();
        root.setTop(topBar);

        // Tab pane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Create tabs
        dashboardController = new DashboardController();
        Tab dashboardTab = new Tab("Dashboard");
        dashboardTab.setContent(dashboardController.createDashboardView());

        budgetController = new BudgetController();

        TransactionsController transactionsController = new TransactionsController(stage, () -> {
            // Refresh dashboard and budget when transactions change
            dashboardController.refresh();
            budgetController.refresh();
        });
        Tab transactionsTab = new Tab("Transactions");
        transactionsTab.setContent(transactionsController.createTransactionsView());

        Tab budgetTab = new Tab("Budget");
        budgetTab.setContent(budgetController.createBudgetView());

        ReportsController reportsController = new ReportsController(stage);
        Tab reportsTab = new Tab("Reports");
        reportsTab.setContent(reportsController.createReportsView());

        tabPane.getTabs().addAll(dashboardTab, transactionsTab, budgetTab, reportsTab);

        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1400, 800);
        scene.getStylesheets().add(getClass().getResource("/com/example/mymoney/styles.css").toExternalForm());
        return scene;
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");

        Label welcomeLabel = new Label("Welcome, " + authService.getCurrentUser().getUsername() + "!");
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("danger-button");
        logoutButton.setOnAction(e -> logout());

        topBar.getChildren().addAll(welcomeLabel, spacer, logoutButton);
        return topBar;
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
