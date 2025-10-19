module com.example.mymoney {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.sql;

    opens com.example.mymoney to javafx.fxml;
    opens com.example.mymoney.controller to javafx.fxml;
    opens com.example.mymoney.model to javafx.base;
    opens com.example.mymoney.service to javafx.base;
    
    exports com.example.mymoney;
    exports com.example.mymoney.controller;
    exports com.example.mymoney.model;
    exports com.example.mymoney.service;
    exports com.example.mymoney.database;
}