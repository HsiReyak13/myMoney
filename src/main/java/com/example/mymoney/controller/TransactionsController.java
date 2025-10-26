package com.example.mymoney.controller;

import com.example.mymoney.model.Transaction;
import com.example.mymoney.service.AuthenticationService;
import com.example.mymoney.service.DataService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionsController {
    private final AuthenticationService authService;
    private final DataService dataService;
    private final Runnable onTransactionChange;
    private final Stage stage;

    private ToggleGroup typeToggleGroup;
    private ToggleButton incomeToggle;
    private ToggleButton expenseToggle;
    private Label incomeTotalLabel;
    private Label expenseTotalLabel;
    private DatePicker fromDate;
    private DatePicker toDate;
    private ComboBox<String> categoryFilter;
    private TextField searchField;
    private TextField minAmount;
    private TextField maxAmount;
    private TableView<Transaction> transactionTable;
    private ObservableList<Transaction> masterList = FXCollections.observableArrayList();
    private ObservableList<Transaction> filteredList = FXCollections.observableArrayList();

    public TransactionsController(Stage stage, Runnable onTransactionChange) {
        this.authService = AuthenticationService.getInstance();
        this.dataService = DataService.getInstance();
        this.onTransactionChange = onTransactionChange;
        this.stage = stage;
    }

    public VBox createTransactionsView() {
        try {
            // Try to load from FXML
            var resource = getClass().getResource("/com/example/mymoney/transactions-view.fxml");
            if (resource == null) {
                System.err.println("FXML resource not found, building UI programmatically");
                return buildTransactionsViewProgrammatically();
            }
            
            FXMLLoader loader = new FXMLLoader(resource);
            VBox root = loader.load();
            
            // Wire up UI elements from FXML
            incomeToggle = (ToggleButton) root.lookup("#incomeToggle");
            expenseToggle = (ToggleButton) root.lookup("#expenseToggle");
            incomeTotalLabel = (Label) root.lookup("#incomeTotalLabel");
            expenseTotalLabel = (Label) root.lookup("#expenseTotalLabel");
            fromDate = (DatePicker) root.lookup("#fromDate");
            toDate = (DatePicker) root.lookup("#toDate");
            categoryFilter = (ComboBox<String>) root.lookup("#categoryFilter");
            searchField = (TextField) root.lookup("#searchField");
            minAmount = (TextField) root.lookup("#minAmount");
            maxAmount = (TextField) root.lookup("#maxAmount");
            transactionTable = (TableView<Transaction>) root.lookup("#transactionsTable");
            Button exportBtn = (Button) root.lookup("#exportBtn");
            Button addBtn = (Button) root.lookup("#addBtn");
            Button clearFiltersBtn = (Button) root.lookup("#clearFiltersBtn");
            PieChart categoryChart = (PieChart) root.lookup("#categoryChart");
            LineChart<Number, Number> trendsChart = (LineChart<Number, Number>) root.lookup("#trendsChart");

            // Validate all UI elements were loaded
            if (transactionTable == null) {
                System.err.println("transactionTable is null, building UI programmatically");
                return buildTransactionsViewProgrammatically();
            }

            // Setup table columns
            setupTableColumns();
            transactionTable.setItems(filteredList);

            // Setup toggle group
            typeToggleGroup = new ToggleGroup();
            incomeToggle.setToggleGroup(typeToggleGroup);
            expenseToggle.setToggleGroup(typeToggleGroup);
            expenseToggle.setSelected(true);

            // Wire button actions
            exportBtn.setOnAction(e -> exportToCSV());
            addBtn.setOnAction(e -> showAddTransactionDialog());
            clearFiltersBtn.setOnAction(e -> clearFilters());

            // Load data and setup events
            refreshData();
            setupFilterEvents();
            setupCharts(categoryChart, trendsChart);

            return root;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException loading FXML: " + e.getMessage());
            return buildTransactionsViewProgrammatically();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception loading FXML: " + e.getMessage());
            return buildTransactionsViewProgrammatically();
        }
    }

    private VBox buildTransactionsViewProgrammatically() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        // Header with Toggle and Totals
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        incomeToggle = new ToggleButton("Income");
        expenseToggle = new ToggleButton("Expenses");
        incomeToggle.getStyleClass().add("segmented-toggle");
        expenseToggle.getStyleClass().add("segmented-toggle");
        typeToggleGroup = new ToggleGroup();
        incomeToggle.setToggleGroup(typeToggleGroup);
        expenseToggle.setToggleGroup(typeToggleGroup);
        expenseToggle.setSelected(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        incomeTotalLabel = new Label("Income: $0");
        expenseTotalLabel = new Label("Expenses: $0");

        Button exportBtn = new Button("Export CSV");
        exportBtn.getStyleClass().add("secondary-button");
        exportBtn.setOnAction(e -> exportToCSV());

        Button addBtn = new Button("+ Add");
        addBtn.getStyleClass().add("primary-button");
        addBtn.setOnAction(e -> showAddTransactionDialog());

        header.getChildren().addAll(incomeToggle, expenseToggle, spacer, incomeTotalLabel, expenseTotalLabel, exportBtn, addBtn);

        // Filters Row
        HBox filters = new HBox(10);
        filters.setAlignment(Pos.CENTER_LEFT);
        fromDate = new DatePicker();
        fromDate.setPromptText("From");
        toDate = new DatePicker();
        toDate.setPromptText("To");
        categoryFilter = new ComboBox<>();
        categoryFilter.setPromptText("Category");
        categoryFilter.setPrefWidth(160);
        searchField = new TextField();
        searchField.setPromptText("Search notes, merchant");
        searchField.setPrefWidth(240);
        minAmount = new TextField();
        minAmount.setPromptText("Min");
        minAmount.setPrefWidth(90);
        maxAmount = new TextField();
        maxAmount.setPromptText("Max");
        maxAmount.setPrefWidth(90);
        Button clearFiltersBtn = new Button("Clear");
        clearFiltersBtn.setOnAction(e -> clearFilters());
        filters.getChildren().addAll(fromDate, toDate, categoryFilter, searchField, minAmount, maxAmount, clearFiltersBtn);

        // Table
        transactionTable = new TableView<>();
        transactionTable.getStyleClass().add("table-view");
        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        transactionTable.setPrefHeight(500);
        setupTableColumns();
        transactionTable.setItems(filteredList);

        VBox.setVgrow(transactionTable, Priority.ALWAYS);

        root.getChildren().addAll(header, filters, transactionTable);

        // Load data and setup events
        refreshData();
        setupFilterEvents();

        return root;
    }

    private void setupTableColumns() {
        TableColumn<Transaction, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedDate()));

        TableColumn<Transaction, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Transaction, String> noteColumn = new TableColumn<>("Note");
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));

        TableColumn<Transaction, String> accountColumn = new TableColumn<>("Account");
        accountColumn.setCellValueFactory(new PropertyValueFactory<>("account"));

        TableColumn<Transaction, String> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(cellData -> {
            Transaction t = cellData.getValue();
            String prefix = t.getType() == Transaction.TransactionType.INCOME ? "+" : "-";
            return new SimpleStringProperty(prefix + t.getFormattedAmount());
        });
        amountColumn.setCellFactory(column -> new TableCell<Transaction, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    setStyle(item.startsWith("+") ? "-fx-text-fill: #06ffa5; -fx-font-weight: bold; -fx-alignment: CENTER-RIGHT;" : "-fx-text-fill: #e63946; -fx-font-weight: bold; -fx-alignment: CENTER-RIGHT;");
                }
            }
        });

        transactionTable.getColumns().setAll(dateColumn, categoryColumn, noteColumn, amountColumn, accountColumn);
    }

    private void setupCharts(PieChart categoryChart, LineChart<Number, Number> trendsChart) {
        // Update charts when filtered list changes
        filteredList.addListener((javafx.collections.ListChangeListener<Transaction>) change -> {
            updateCategoryChart(categoryChart);
            updateTrendsChart(trendsChart);
        });
        
        // Initial chart update
        updateCategoryChart(categoryChart);
        updateTrendsChart(trendsChart);
    }

    private void updateCategoryChart(PieChart chart) {
        Map<String, Double> categoryTotals = new HashMap<>();
        for (Transaction t : filteredList) {
            categoryTotals.merge(t.getCategory(), t.getAmount(), Double::sum);
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        chart.setData(pieData);
        chart.setStyle("-fx-background-color: transparent;");
    }

    private void updateTrendsChart(LineChart<Number, Number> chart) {
        // Group transactions by month and sum amounts
        Map<YearMonth, Double> monthlyTotals = new TreeMap<>();
        for (Transaction t : filteredList) {
            YearMonth month = YearMonth.from(t.getDate());
            monthlyTotals.merge(month, t.getAmount(), Double::sum);
        }

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(typeToggleGroup.getSelectedToggle() == incomeToggle ? "Income" : "Expenses");
        
        int index = 0;
        for (Map.Entry<YearMonth, Double> entry : monthlyTotals.entrySet()) {
            series.getData().add(new XYChart.Data<>(index++, entry.getValue()));
        }

        chart.getData().clear();
        chart.getData().add(series);
        chart.setStyle("-fx-background-color: transparent;");
    }

    private void showAddTransactionDialog() {
        AddTransactionDialog dialog = new AddTransactionDialog(stage);
        Transaction transaction = dialog.showAndWait();
        if (transaction == null) return;
        dataService.addTransaction(transaction);
        refreshData();
        onTransactionChange.run();
        new Alert(Alert.AlertType.INFORMATION, "Transaction added successfully!", ButtonType.OK).showAndWait();
    }

    private void exportToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Transaction Data");
        fileChooser.setInitialFileName("transactions_" + LocalDate.now() + ".csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;
        try {
            String userId = authService.getCurrentUser().getId();
            dataService.exportToCSV(userId, file.getAbsolutePath());
            new Alert(Alert.AlertType.INFORMATION, "Transaction data exported successfully!", ButtonType.OK).showAndWait();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to export data: " + e.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    private void refreshData() {
        String userId = authService.getCurrentUser().getId();
        masterList.setAll(dataService.getTransactionsForUser(userId));
        applyFilters();
    }

    private void setupFilterEvents() {
        typeToggleGroup.selectedToggleProperty().addListener((obs, o, n) -> applyFilters());

        fromDate.valueProperty().addListener((obs, o, n) -> applyFilters());
        toDate.valueProperty().addListener((obs, o, n) -> applyFilters());
        categoryFilter.valueProperty().addListener((obs, o, n) -> applyFilters());
        searchField.textProperty().addListener((obs, o, n) -> applyFilters());
        minAmount.textProperty().addListener((obs, o, n) -> applyFilters());
        maxAmount.textProperty().addListener((obs, o, n) -> applyFilters());
    }

    private void clearFilters() {
        fromDate.setValue(null);
        toDate.setValue(null);
        categoryFilter.getSelectionModel().clearSelection();
        searchField.clear();
        minAmount.clear();
        maxAmount.clear();
        applyFilters();
    }

    private void applyFilters() {
        final boolean showIncome = incomeToggle.isSelected() && !expenseToggle.isSelected();
        final boolean showExpense = expenseToggle.isSelected() && !incomeToggle.isSelected();
        // Default to expense view
        final boolean finalShowExpense = (!showIncome && !showExpense) || showExpense;

        filteredList.setAll(masterList.filtered(t -> {
            if (showIncome && t.getType() != Transaction.TransactionType.INCOME) return false;
            if (finalShowExpense && t.getType() != Transaction.TransactionType.EXPENSE) return false;

            if (fromDate.getValue() != null && t.getDate().isBefore(fromDate.getValue())) return false;
            if (toDate.getValue() != null && t.getDate().isAfter(toDate.getValue())) return false;

            String sel = categoryFilter.getSelectionModel().getSelectedItem();
            if (sel != null && !sel.isBlank() && !sel.equals(t.getCategory())) return false;

            String q = searchField.getText();
            if (q != null && !q.isBlank()) {
                String v = q.toLowerCase();
                String notes = t.getNotes() != null ? t.getNotes().toLowerCase() : "";
                String cat = t.getCategory() != null ? t.getCategory().toLowerCase() : "";
                if (!notes.contains(v) && !cat.contains(v)) return false;
            }

            try {
                if (!minAmount.getText().isBlank()) {
                    double min = Double.parseDouble(minAmount.getText());
                    if (t.getAmount() < min) return false;
                }
                if (!maxAmount.getText().isBlank()) {
                    double max = Double.parseDouble(maxAmount.getText());
                    if (t.getAmount() > max) return false;
                }
            } catch (NumberFormatException ignored) {}

            return true;
        }));

        updateTotals();
    }

    private void updateTotals() {
        double income = masterList.stream().filter(t -> t.getType() == Transaction.TransactionType.INCOME).mapToDouble(Transaction::getAmount).sum();
        double expense = masterList.stream().filter(t -> t.getType() == Transaction.TransactionType.EXPENSE).mapToDouble(Transaction::getAmount).sum();
        incomeTotalLabel.setText("Income: +$" + String.format("%.2f", income));
        expenseTotalLabel.setText("Expenses: -$" + String.format("%.2f", expense));
    }
}
