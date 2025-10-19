package com.example.mymoney.controller;

import com.example.mymoney.service.AuthenticationService;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WelcomeController {
    private final Stage stage;
    private final AuthenticationService authService;
    private VBox rightSide;
    private VBox originalRightContent;

    public WelcomeController(Stage stage) {
        this.stage = stage;
        this.authService = AuthenticationService.getInstance();
    }

    public Scene createWelcomeScene() {
        HBox mainContainer = new HBox(0);
        mainContainer.setPrefSize(1200, 700);
        
        VBox leftSide = new VBox(30);
        leftSide.getStyleClass().add("welcome-left-side");
        leftSide.setAlignment(Pos.CENTER);
        leftSide.setPrefWidth(600);
        HBox.setHgrow(leftSide, Priority.ALWAYS);
        
        Region topSpacerLeft = new Region();
        VBox.setVgrow(topSpacerLeft, Priority.ALWAYS);
        
        Text welcomeTitle = new Text("Welcome to MyMoney");
        welcomeTitle.getStyleClass().add("welcome-main-title");
        
        Text welcomeDesc = new Text("Take control of your finances with our comprehensive personal\n" +
                "finance management tool. Track expenses, manage budgets, and\n" +
                "achieve your financial goals.");
        welcomeDesc.getStyleClass().add("welcome-description");
        welcomeDesc.setWrappingWidth(500);
        welcomeDesc.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        VBox leftFeaturesContainer = new VBox(15);
        leftFeaturesContainer.setAlignment(Pos.CENTER);
        leftFeaturesContainer.setMaxWidth(500);
        
        VBox leftFeatures = new VBox(12);
        leftFeatures.setAlignment(Pos.CENTER_LEFT);
        leftFeatures.setMaxWidth(350);
        
        Label feature1Left = new Label("💰   Track Income & Expenses");
        feature1Left.getStyleClass().add("feature-item-left");
        
        Label feature2Left = new Label("📊   Visual Financial Reports");
        feature2Left.getStyleClass().add("feature-item-left");
        
        Label feature3Left = new Label("🎯   Budget Management");
        feature3Left.getStyleClass().add("feature-item-left");
        
        Label feature4Left = new Label("📧   Financial Analytics");
        feature4Left.getStyleClass().add("feature-item-left");
        
        leftFeatures.getChildren().addAll(feature1Left, feature2Left, feature3Left, feature4Left);
        leftFeaturesContainer.getChildren().add(leftFeatures);
        
        Region bottomSpacerLeft = new Region();
        VBox.setVgrow(bottomSpacerLeft, Priority.ALWAYS);
        
        Text footerCredit = new Text("designed by MyMoney Team");
        footerCredit.getStyleClass().add("footer-credit");
        
        leftSide.getChildren().addAll(topSpacerLeft, welcomeTitle, welcomeDesc, leftFeaturesContainer, bottomSpacerLeft, footerCredit);
        
        rightSide = new VBox(30);
        rightSide.getStyleClass().add("welcome-right-side");
        rightSide.setAlignment(Pos.CENTER);
        rightSide.setPrefWidth(600);
        HBox.setHgrow(rightSide, Priority.ALWAYS);
        
        originalRightContent = createRightSideContent();
        
        rightSide.getChildren().add(originalRightContent);
        
        mainContainer.getChildren().addAll(leftSide, rightSide);
        
        Scene scene = new Scene(mainContainer);
        scene.getStylesheets().add(getClass().getResource("/com/example/mymoney/styles.css").toExternalForm());
        return scene;
    }

    private VBox createRightSideContent() {
        VBox content = new VBox(35);
        content.setAlignment(Pos.CENTER);
        VBox.setVgrow(content, Priority.ALWAYS);
        
        Region topSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);
        
        Text getStartedTitle = new Text("GET STARTED");
        getStartedTitle.getStyleClass().add("get-started-title");
        
        Text getStartedDesc = new Text("Choose your path to financial freedom.\nJoin thousands managing their money smarter.");
        getStartedDesc.getStyleClass().add("get-started-description");
        getStartedDesc.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        Button signUpButton = new Button("SIGN UP FREE");
        signUpButton.getStyleClass().add("primary-cta-button");
        signUpButton.setOnAction(e -> showSignupForm());
        signUpButton.setPrefWidth(280);
        signUpButton.setPrefHeight(55);
        
        VBox signInSection = new VBox(8);
        signInSection.setAlignment(Pos.CENTER);
        
        Label alreadyHaveAccount = new Label("Already have an account?");
        alreadyHaveAccount.getStyleClass().add("secondary-label");
        
        Button signInButton = new Button("Sign In");
        signInButton.getStyleClass().add("secondary-cta-button");
        signInButton.setOnAction(e -> showLoginForm());
        signInButton.setPrefWidth(280);
        
        signInSection.getChildren().addAll(alreadyHaveAccount, signInButton);
        
        VBox buttonBox = new VBox(20, signUpButton, signInSection);
        buttonBox.setAlignment(Pos.CENTER);
        
        Label socialProof = new Label("✓ No credit card required  •  ✓ Free forever");
        socialProof.getStyleClass().add("social-proof-label");
        
        Region bottomSpacer = new Region();
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);
        
        content.getChildren().addAll(topSpacer, getStartedTitle, getStartedDesc, buttonBox, socialProof, bottomSpacer);
        
        return content;
    }

    private void showLoginForm() {
        rightSide.getChildren().clear();

        VBox formContainer = new VBox(15);
        formContainer.getStyleClass().add("form-container");
        formContainer.setMaxWidth(400);
        formContainer.setAlignment(Pos.CENTER);

        Text formTitle = new Text("Login");
        formTitle.getStyleClass().add("form-title");

        Label usernameLabel = new Label("Username:");
        usernameLabel.getStyleClass().add("field-label");
        TextField usernameField = new TextField();
        usernameField.getStyleClass().add("text-field");
        usernameField.setPromptText("Enter username");

        Label passwordLabel = new Label("Password:");
        passwordLabel.getStyleClass().add("field-label");
        PasswordField passwordField = new PasswordField();
        passwordField.getStyleClass().add("password-field");
        passwordField.setPromptText("Enter password");

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        Button submitButton = new Button("Login");
        submitButton.getStyleClass().add("primary-button");
        submitButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please fill in all fields");
                errorLabel.setVisible(true);
                return;
            }

            if (authService.login(username, password)) {
                openMainApplication();
            } else {
                errorLabel.setText("Invalid username or password");
                errorLabel.setVisible(true);
            }
        });

        Button backButton = new Button("Back");
        backButton.getStyleClass().add("secondary-button");
        backButton.setOnAction(e -> restoreWelcomeScreen());

        HBox buttonBox = new HBox(15, submitButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        formContainer.getChildren().addAll(
            formTitle,
            usernameLabel, usernameField,
            passwordLabel, passwordField,
            errorLabel,
            buttonBox
        );

        rightSide.getChildren().add(formContainer);
    }

    private void showSignupForm() {
        rightSide.getChildren().clear();

        VBox formContainer = new VBox(15);
        formContainer.getStyleClass().add("form-container");
        formContainer.setMaxWidth(400);
        formContainer.setAlignment(Pos.CENTER);

        Text formTitle = new Text("Sign Up");
        formTitle.getStyleClass().add("form-title");

        Label usernameLabel = new Label("Username:");
        usernameLabel.getStyleClass().add("field-label");
        TextField usernameField = new TextField();
        usernameField.getStyleClass().add("text-field");
        usernameField.setPromptText("Choose a username");

        Label passwordLabel = new Label("Password:");
        passwordLabel.getStyleClass().add("field-label");
        PasswordField passwordField = new PasswordField();
        passwordField.getStyleClass().add("password-field");
        passwordField.setPromptText("Choose a password");

        Label confirmPasswordLabel = new Label("Confirm Password:");
        confirmPasswordLabel.getStyleClass().add("field-label");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.getStyleClass().add("password-field");
        confirmPasswordField.setPromptText("Re-enter password");

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        Button submitButton = new Button("Sign Up");
        submitButton.getStyleClass().add("primary-button");
        submitButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                errorLabel.setText("Please fill in all fields");
                errorLabel.setVisible(true);
                return;
            }

            if (username.length() < 3) {
                errorLabel.setText("Username must be at least 3 characters");
                errorLabel.setVisible(true);
                return;
            }

            if (password.length() < 6) {
                errorLabel.setText("Password must be at least 6 characters");
                errorLabel.setVisible(true);
                return;
            }

            if (!password.equals(confirmPassword)) {
                errorLabel.setText("Passwords do not match");
                errorLabel.setVisible(true);
                return;
            }

            if (authService.register(username, password)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Account created successfully! Please login.");
                alert.showAndWait();
                
                restoreWelcomeScreen();
            } else {
                errorLabel.setText("Username already exists");
                errorLabel.setVisible(true);
            }
        });

        Button backButton = new Button("Back");
        backButton.getStyleClass().add("secondary-button");
        backButton.setOnAction(e -> restoreWelcomeScreen());

        HBox buttonBox = new HBox(15, submitButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        formContainer.getChildren().addAll(
            formTitle,
            usernameLabel, usernameField,
            passwordLabel, passwordField,
            confirmPasswordLabel, confirmPasswordField,
            errorLabel,
            buttonBox
        );

        rightSide.getChildren().add(formContainer);
    }

    private void restoreWelcomeScreen() {
        rightSide.getChildren().clear();
        rightSide.getChildren().add(originalRightContent);
    }

    private void openMainApplication() {
        try {
            MainController mainController = new MainController(stage);
            Scene mainScene = mainController.createMainScene();
            stage.setScene(mainScene);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to open main application: " + e.getMessage());
            alert.showAndWait();
        }
    }
}

