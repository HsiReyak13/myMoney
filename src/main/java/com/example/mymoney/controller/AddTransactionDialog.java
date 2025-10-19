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

public class AddTransactionDialog {
    private final Stage dialog;
    private Transaction result = null;
    private final AuthenticationService authService;

    public AddTransactionDialog(Stage owner) {
        this.authService = AuthenticationService.getInstance();
        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setTitle("Add Transaction");

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

        // Header with close button
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Text title = new Text("Add New Transaction");
        title.getStyleClass().add("form-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeButton = new Button("×");
        closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 24px; -fx-cursor: hand;");
        closeButton.setOnAction(e -> dialog.close());

        header.getChildren().addAll(title, spacer, closeButton);

        // Type
        Label typeLabel = new Label("Type");
        typeLabel.getStyleClass().add("field-label");
        typeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Expense", "Income");
        typeCombo.setValue("Expense");
        typeCombo.getStyleClass().add("combo-box");
        typeCombo.setMaxWidth(Double.MAX_VALUE);
        typeCombo.setPrefHeight(40);

        // Date
        Label dateLabel = new Label("Date");
        dateLabel.getStyleClass().add("field-label");
        dateLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.getStyleClass().add("date-picker");
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setPrefHeight(40);

        // Category
        Label categoryLabel = new Label("Category");
        categoryLabel.getStyleClass().add("field-label");
        categoryLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getStyleClass().add("combo-box");
        categoryCombo.setMaxWidth(Double.MAX_VALUE);
        categoryCombo.setPrefHeight(40);

        // Update categories based on type
        typeCombo.setOnAction(e -> {
            categoryCombo.getItems().clear();
            if (typeCombo.getValue().equals("Income")) {
                categoryCombo.getItems().addAll("Salary", "Freelance", "Investment", "Gift", "Other Income");
            } else {
                categoryCombo.getItems().addAll("Groceries", "Utilities", "Transportation", "Entertainment", "Healthcare", "Shopping", "Food", "Other Expense");
            }
            if (!categoryCombo.getItems().isEmpty()) {
                categoryCombo.setValue(categoryCombo.getItems().get(0));
            }
        });

        // Initialize categories
        categoryCombo.getItems().addAll("Groceries", "Utilities", "Transportation", "Entertainment", "Healthcare", "Shopping", "Food", "Other Expense");
        categoryCombo.setValue("Groceries");

        // Amount
        Label amountLabel = new Label("Amount");
        amountLabel.getStyleClass().add("field-label");
        amountLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        TextField amountField = new TextField();
        amountField.setPromptText("0.00");
        amountField.getStyleClass().add("text-field");
        amountField.setPrefHeight(40);

        // Note
        Label noteLabel = new Label("Note");
        noteLabel.getStyleClass().add("field-label");
        noteLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        TextField noteField = new TextField();
        noteField.setPromptText("Add a note");
        noteField.getStyleClass().add("text-field");
        noteField.setPrefHeight(40);

        // Error label
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(Double.MAX_VALUE);

        // Spacer before buttons
        Region buttonSpacer = new Region();
        buttonSpacer.setPrefHeight(5);

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(5, 0, 0, 0));

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("secondary-button");
        cancelButton.setPrefWidth(200);
        cancelButton.setPrefHeight(45);
        cancelButton.setOnAction(e -> dialog.close());

        Button addButton = new Button("Add Transaction");
        addButton.getStyleClass().add("primary-button");
        addButton.setPrefWidth(200);
        addButton.setPrefHeight(45);
        addButton.setOnAction(e -> {
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
                String notes = noteField.getText().trim();

                result = new Transaction(
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

        buttonBox.getChildren().addAll(cancelButton, addButton);

        dialogBox.getChildren().addAll(
            header,
            typeLabel, typeCombo,
            dateLabel, datePicker,
            categoryLabel, categoryCombo,
            amountLabel, amountField,
            noteLabel, noteField,
            errorLabel,
            buttonSpacer,
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

