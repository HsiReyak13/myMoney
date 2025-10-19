package com.example.mymoney.controller;

import com.example.mymoney.model.Transaction;
import com.example.mymoney.service.AuthenticationService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;

import java.time.LocalDate;

public class EditTransactionDialog {
    private final Stage dialog;
    private Transaction result = null;
    private final AuthenticationService authService;
    private final Transaction existingTransaction;

    public EditTransactionDialog(Stage owner, Transaction transaction) {
        this.authService = AuthenticationService.getInstance();
        this.existingTransaction = transaction;
        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setTitle("Edit Transaction");

        VBox root = createDialogContent();
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/com/example/mymoney/styles.css").toExternalForm());
        dialog.setScene(scene);
    }

    private VBox createDialogContent() {
        VBox container = new VBox();
        container.setStyle("-fx-background-color: transparent;");
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(50));
        container.setMinSize(600, 700);
        container.setMaxSize(600, 700);

        VBox dialogBox = new VBox(15);
        dialogBox.getStyleClass().add("form-container");
        dialogBox.setMinWidth(480);
        dialogBox.setMaxWidth(480);
        dialogBox.setPadding(new Insets(25));

        Text title = new Text("Edit Transaction");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-fill: white;");

        // Type ComboBox
        Label typeLabel = new Label("Type");
        typeLabel.getStyleClass().add("form-label");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Income", "Expense");
        typeCombo.setValue(existingTransaction.getType() == Transaction.TransactionType.INCOME ? "Income" : "Expense");
        typeCombo.setPrefWidth(430);
        typeCombo.getStyleClass().add("form-input");

        // Date Picker
        Label dateLabel = new Label("Date");
        dateLabel.getStyleClass().add("form-label");
        DatePicker datePicker = new DatePicker(existingTransaction.getDate());
        datePicker.setPrefWidth(430);
        datePicker.getStyleClass().add("form-input");

        // Category ComboBox
        Label categoryLabel = new Label("Category");
        categoryLabel.getStyleClass().add("form-label");
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.setPrefWidth(430);
        categoryCombo.getStyleClass().add("form-input");
        categoryCombo.setValue(existingTransaction.getCategory());

        // Update categories based on type
        typeCombo.setOnAction(e -> {
            categoryCombo.getItems().clear();
            if (typeCombo.getValue().equals("Income")) {
                categoryCombo.getItems().addAll("Salary", "Freelance", "Investment", "Business", "Gift", "Other Income");
            } else {
                categoryCombo.getItems().addAll("Groceries", "Rent", "Utilities", "Transportation", "Entertainment", 
                                                "Healthcare", "Shopping", "Dining", "Education", "Other Expense");
            }
            if (!categoryCombo.getItems().contains(existingTransaction.getCategory())) {
                categoryCombo.setValue(categoryCombo.getItems().get(0));
            }
        });

        // Initialize categories
        if (typeCombo.getValue().equals("Income")) {
            categoryCombo.getItems().addAll("Salary", "Freelance", "Investment", "Business", "Gift", "Other Income");
        } else {
            categoryCombo.getItems().addAll("Groceries", "Rent", "Utilities", "Transportation", "Entertainment", 
                                            "Healthcare", "Shopping", "Dining", "Education", "Other Expense");
        }

        // Amount Field
        Label amountLabel = new Label("Amount");
        amountLabel.getStyleClass().add("form-label");
        TextField amountField = new TextField(String.valueOf(existingTransaction.getAmount()));
        amountField.setPromptText("Enter amount");
        amountField.setPrefWidth(430);
        amountField.getStyleClass().add("form-input");

        // Note Field
        Label noteLabel = new Label("Note");
        noteLabel.getStyleClass().add("form-label");
        TextArea noteField = new TextArea(existingTransaction.getNotes());
        noteField.setPromptText("Enter optional note");
        noteField.setPrefWidth(430);
        noteField.setPrefHeight(100);
        noteField.setWrapText(true);
        noteField.getStyleClass().add("form-input");

        // Error Label
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #e63946; -fx-font-size: 13px;");
        errorLabel.setVisible(false);

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("secondary-button");
        cancelButton.setPrefWidth(200);
        cancelButton.setPrefHeight(45);
        cancelButton.setOnAction(e -> dialog.close());

        Button updateButton = new Button("Update Transaction");
        updateButton.getStyleClass().add("primary-button");
        updateButton.setPrefWidth(200);
        updateButton.setPrefHeight(45);
        updateButton.setOnAction(e -> {
            try {
                String amountText = amountField.getText().trim();
                if (amountText.isEmpty()) {
                    errorLabel.setText("Please enter an amount");
                    errorLabel.setVisible(true);
                    return;
                }

                double amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    errorLabel.setText("Amount must be positive");
                    errorLabel.setVisible(true);
                    return;
                }

                Transaction.TransactionType type = typeCombo.getValue().equals("Income") 
                    ? Transaction.TransactionType.INCOME 
                    : Transaction.TransactionType.EXPENSE;

                String category = categoryCombo.getValue();
                if (category == null || category.isEmpty()) {
                    errorLabel.setText("Please select a category");
                    errorLabel.setVisible(true);
                    return;
                }

                LocalDate date = datePicker.getValue();
                if (date == null) {
                    errorLabel.setText("Please select a date");
                    errorLabel.setVisible(true);
                    return;
                }
                
                String notes = noteField.getText().trim();
                
                if (notes.length() > 500) {
                    errorLabel.setText("Notes must be 500 characters or less");
                    errorLabel.setVisible(true);
                    return;
                }

                result = new Transaction(
                    existingTransaction.getId(), // Keep same ID
                    authService.getCurrentUser().getId(),
                    type,
                    amount,
                    category,
                    notes,
                    date
                );

                dialog.close();
            } catch (NumberFormatException ex) {
                errorLabel.setText("Invalid amount format");
                errorLabel.setVisible(true);
            }
        });

        buttonBox.getChildren().addAll(cancelButton, updateButton);

        dialogBox.getChildren().addAll(
            title,
            typeLabel, typeCombo,
            dateLabel, datePicker,
            categoryLabel, categoryCombo,
            amountLabel, amountField,
            noteLabel, noteField,
            errorLabel,
            buttonBox
        );

        container.getChildren().add(dialogBox);
        return container;
    }

    public Transaction showAndWait() {
        dialog.showAndWait();
        return result;
    }
}

