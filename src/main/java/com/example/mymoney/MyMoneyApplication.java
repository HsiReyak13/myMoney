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

        WelcomeController welcomeController = new WelcomeController(stage);
        Scene scene = welcomeController.createWelcomeScene();
        
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

