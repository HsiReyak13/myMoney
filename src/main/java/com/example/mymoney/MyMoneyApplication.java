package com.example.mymoney;

import com.example.mymoney.controller.MainController;
import com.example.mymoney.controller.WelcomeController;
import com.example.mymoney.service.AuthenticationService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class    MyMoneyApplication extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("MyMoney - Personal Finance Manager");
        stage.setMinWidth(900);
        stage.setMinHeight(600);

        // AUTO-LOGIN FOR DEVELOPMENT (BYPASS LOGIN SCREEN)
        // Uncomment below to auto-login without using welcome screen
        /*
        AuthenticationService authService = AuthenticationService.getInstance();
        
        // Try to register, but ignore if already exists
        boolean registered = authService.register("testuser", "password123");
        if (registered) {
            System.out.println("✅ Test user created");
        }
        
        // Auto-login with the test user
        boolean loggedIn = authService.login("testuser", "password123");
        
        if (!loggedIn) {
            System.err.println("❌ Auto-login failed! Showing welcome screen instead...");
            WelcomeController welcomeController = new WelcomeController(stage);
            Scene scene = welcomeController.createWelcomeScene();
            stage.setScene(scene);
            stage.show();
            return;
        }
        
        System.out.println("✅ Auto-logged in as: testuser");
        
        // Go directly to main application
        MainController mainController = new MainController(stage);
        Scene scene = mainController.createMainScene();
        */
        
        // Show welcome screen (login/signup)
        WelcomeController welcomeController = new WelcomeController(stage);
        Scene scene = welcomeController.createWelcomeScene();
        
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

