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

        Button exportButton = new Button("Export CSV");
        exportButton.getStyleClass().add("secondary-button");
        exportButton.setOnAction(e -> exportToCSV());

        Button addButton = new Button("+ Add Transaction");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(e -> showAddTransactionDialog());

        header.getChildren().addAll(title, spacer, exportButton, addButton);
        return header;
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
        noteColumn.setPrefWidth(250);

        // ✅ New: Actions column with Edit/Delete buttons
        TableColumn<Transaction, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setPrefWidth(150);
        actionsColumn.setCellFactory(column -> new TableCell<Transaction, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonsBox = new HBox(5, editButton, deleteButton);

            {
                editButton.getStyleClass().add("secondary-button");
                editButton.setStyle("-fx-font-size: 11px; -fx-padding: 5 10;");
                deleteButton.getStyleClass().add("secondary-button");
                deleteButton.setStyle("-fx-font-size: 11px; -fx-padding: 5 10; -fx-background-color: #e63946;");
                buttonsBox.setAlignment(Pos.CENTER);

                editButton.setOnAction(e -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    showEditTransactionDialog(transaction);
                });

                deleteButton.setOnAction(e -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(transaction);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonsBox);
                }
            }
        });

        transactionTable.getColumns().addAll(dateColumn, categoryColumn, amountColumn, noteColumn, actionsColumn);

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
    
    // ✅ New: Show edit transaction dialog
    private void showEditTransactionDialog(Transaction transaction) {
        EditTransactionDialog dialog = new EditTransactionDialog(stage, transaction);
        Transaction updatedTransaction = dialog.showAndWait();

        if (updatedTransaction != null) {
            dataService.updateTransaction(updatedTransaction);
            refreshTable();
            onTransactionChange.run();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Transaction updated successfully!");
            alert.showAndWait();
        }
    }
    
    // ✅ New: Show delete confirmation dialog
    private void showDeleteConfirmation(Transaction transaction) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Transaction");
        confirmAlert.setHeaderText("Are you sure you want to delete this transaction?");
        confirmAlert.setContentText(
            String.format("%s: %s - $%.2f\n%s", 
                transaction.getFormattedDate(),
                transaction.getCategory(),
                transaction.getAmount(),
                transaction.getNotes())
        );

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String userId = authService.getCurrentUser().getId();
                dataService.deleteTransaction(transaction.getId(), userId);
                refreshTable();
                onTransactionChange.run();

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Transaction deleted successfully!");
                successAlert.showAndWait();
            }
        });
    }
}
