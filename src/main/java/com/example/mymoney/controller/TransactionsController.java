package com.example.mymoney.controller;

import com.example.mymoney.model.Transaction;
import com.example.mymoney.service.AuthenticationService;
import com.example.mymoney.service.DataService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionsController {
    private final AuthenticationService authService;
    private final DataService dataService;
    private final Runnable onTransactionChange;
    private final Stage stage;
    private TableView<Transaction> transactionTable;
    private ObservableList<Transaction> transactionList;

    public TransactionsController(Stage stage, Runnable onTransactionChange) {
        this.authService = AuthenticationService.getInstance();
        this.dataService = DataService.getInstance();
        this.onTransactionChange = onTransactionChange;
        this.stage = stage;
    }

    public VBox createTransactionsView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));

        // Header with title and buttons
        HBox header = createHeader();

        // Transaction table
        VBox tableSection = createTransactionTable();

        root.getChildren().addAll(header, tableSection);
        return root;
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Text title = new Text("Transaction History");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // DSA Controls
        HBox dsaControls = createDSAControls();

        Button exportButton = new Button("Export CSV");
        exportButton.getStyleClass().add("secondary-button");
        exportButton.setOnAction(e -> exportToCSV());

        Button addButton = new Button("+ Add Transaction");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(e -> showAddTransactionDialog());

        header.getChildren().addAll(title, spacer, dsaControls, exportButton, addButton);
        return header;
    }

    private HBox createDSAControls() {
        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);

        // Sort ComboBox
        ComboBox<String> sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll(
            "Sort by Date (Merge Sort)",
            "Sort by Amount (Quick Sort)", 
            "Sort by Category (Heap Sort)",
            "Top 5 Highest (Priority Queue)",
            "Top 5 Lowest (Priority Queue)"
        );
        sortCombo.setPromptText("Choose Algorithm");
        sortCombo.setPrefWidth(200);
        sortCombo.setOnAction(e -> applySorting(sortCombo.getValue()));

        // Search Controls
        TextField searchField = new TextField();
        searchField.setPromptText("Search by category or amount...");
        searchField.setPrefWidth(200);
        searchField.setOnAction(e -> performSearch(searchField.getText()));

        Button searchButton = new Button("Search");
        searchButton.getStyleClass().add("secondary-button");
        searchButton.setOnAction(e -> performSearch(searchField.getText()));

        Button clearButton = new Button("Clear");
        clearButton.getStyleClass().add("secondary-button");
        clearButton.setOnAction(e -> {
            searchField.clear();
            refreshTable();
        });

        controls.getChildren().addAll(sortCombo, searchField, searchButton, clearButton);
        return controls;
    }

    private VBox createTransactionTable() {
        VBox section = new VBox(15);

        transactionList = FXCollections.observableArrayList();
        transactionTable = new TableView<>(transactionList);
        transactionTable.getStyleClass().add("table-view");
        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        transactionTable.setPrefHeight(500);

        // Date column
        TableColumn<Transaction, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFormattedDate()));
        dateColumn.setPrefWidth(150);

        // Category column
        TableColumn<Transaction, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryColumn.setPrefWidth(200);

        // Amount column with color coding
        TableColumn<Transaction, String> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(cellData -> {
            Transaction t = cellData.getValue();
            String prefix = t.getType() == Transaction.TransactionType.INCOME ? "+" : "-";
            return new javafx.beans.property.SimpleStringProperty(prefix + t.getFormattedAmount());
        });
        amountColumn.setCellFactory(column -> new TableCell<Transaction, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.startsWith("+")) {
                        setStyle("-fx-text-fill: #06ffa5; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #e63946; -fx-font-weight: bold;");
                    }
                }
            }
        });
        amountColumn.setPrefWidth(150);

        // Note column
        TableColumn<Transaction, String> noteColumn = new TableColumn<>("Note");
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        noteColumn.setPrefWidth(300);

        transactionTable.getColumns().addAll(dateColumn, categoryColumn, amountColumn, noteColumn);

        refreshTable();

        VBox.setVgrow(transactionTable, Priority.ALWAYS);
        section.getChildren().add(transactionTable);

        return section;
    }

    private void showAddTransactionDialog() {
        AddTransactionDialog dialog = new AddTransactionDialog(stage);
        Transaction transaction = dialog.showAndWait();

        if (transaction != null) {
            dataService.addTransaction(transaction);
            refreshTable();
            onTransactionChange.run();

            // Show success notification
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Transaction added successfully!");
            alert.showAndWait();
        }
    }

    private void exportToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Transaction Data");
        fileChooser.setInitialFileName("transactions_" + LocalDate.now() + ".csv");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                String userId = authService.getCurrentUser().getId();
                dataService.exportToCSV(userId, file.getAbsolutePath());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export Successful");
                alert.setHeaderText(null);
                alert.setContentText("Transaction data exported successfully!");
                alert.showAndWait();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Export Failed");
                alert.setHeaderText(null);
                alert.setContentText("Failed to export data: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    private void refreshTable() {
        String userId = authService.getCurrentUser().getId();
        transactionList.setAll(dataService.getTransactionsForUser(userId));
    }

    // ==================== DSA FUNCTIONALITY METHODS ====================
    
    private void applySorting(String sortType) {
        if (sortType == null) return;
        
        String userId = authService.getCurrentUser().getId();
        List<Transaction> transactions = dataService.getTransactionsForUser(userId);
        List<Transaction> sortedTransactions = new ArrayList<>();
        
        long startTime = System.nanoTime();
        
        switch (sortType) {
            case "Sort by Date (Merge Sort)":
                sortedTransactions = dataService.mergeSortByDate(transactions, false); // Descending
                break;
            case "Sort by Amount (Quick Sort)":
                sortedTransactions = dataService.quickSortByAmount(transactions, false); // Descending
                break;
            case "Sort by Category (Heap Sort)":
                sortedTransactions = dataService.heapSortByCategory(transactions, true); // Ascending
                break;
            case "Top 5 Highest (Priority Queue)":
                sortedTransactions = dataService.getTopNTransactionsByAmount(transactions, 5, true);
                break;
            case "Top 5 Lowest (Priority Queue)":
                sortedTransactions = dataService.getTopNTransactionsByAmount(transactions, 5, false);
                break;
        }
        
        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1_000_000.0; // Convert to milliseconds
        
        transactionList.setAll(sortedTransactions);
        
        // Show algorithm performance info
        showAlgorithmInfo(sortType, executionTime, sortedTransactions.size());
    }
    
    private void performSearch(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            refreshTable();
            return;
        }
        
        String userId = authService.getCurrentUser().getId();
        List<Transaction> allTransactions = dataService.getTransactionsForUser(userId);
        List<Transaction> searchResults = new ArrayList<>();
        
        long startTime = System.nanoTime();
        
        // Try to parse as number for amount search
        try {
            double amount = Double.parseDouble(searchTerm);
            // Sort by amount first for binary search
            List<Transaction> sortedByAmount = dataService.quickSortByAmount(allTransactions, true);
            searchResults = dataService.binarySearchByAmount(sortedByAmount, amount, 0.01);
        } catch (NumberFormatException e) {
            // Search by category using linear search
            searchResults = dataService.linearSearchByCategory(allTransactions, searchTerm);
            
            // Also search in notes using KMP pattern matching
            List<Transaction> noteResults = dataService.searchInNotes(allTransactions, searchTerm);
            searchResults.addAll(noteResults);
            
            // Remove duplicates
            searchResults = searchResults.stream()
                .distinct()
                .collect(java.util.stream.Collectors.toList());
        }
        
        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1_000_000.0;
        
        transactionList.setAll(searchResults);
        
        // Show search performance info
        showSearchInfo(searchTerm, executionTime, searchResults.size());
    }
    
    private void showAlgorithmInfo(String algorithm, double executionTime, int resultCount) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Algorithm Performance");
        alert.setHeaderText(algorithm + " Completed");
        alert.setContentText(String.format(
            "Results: %d transactions\n" +
            "Execution Time: %.3f ms\n" +
            "Algorithm: %s",
            resultCount, executionTime, algorithm
        ));
        alert.showAndWait();
    }
    
    private void showSearchInfo(String searchTerm, double executionTime, int resultCount) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Search Results");
        alert.setHeaderText("Search for: \"" + searchTerm + "\"");
        alert.setContentText(String.format(
            "Found: %d transactions\n" +
            "Search Time: %.3f ms\n" +
            "Algorithm: %s",
            resultCount, executionTime, 
            searchTerm.matches("\\d+(\\.\\d+)?") ? "Binary Search" : "Linear Search + KMP"
        ));
        alert.showAndWait();
    }
}
