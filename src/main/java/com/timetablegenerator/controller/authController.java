package com.timetablegenerator.controller;

import com.timetablegenerator.mainApp;
import com.timetablegenerator.model.userModel;
import com.timetablegenerator.service.authService;
import com.timetablegenerator.util.authSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class authController {
    @FXML
    private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final authService auth = new authService();

//    @FXML
//    private void login() {
//        String email = emailField.getText().trim();
//        String password = passwordField.getText().trim();
//
//        if (email.isEmpty() || password.isEmpty()) {
//            messageLabel.setText("Email and password are required");
//            return;
//        }
//
//        userModel user = auth.login(email, password);
//
//        if (user != null) {
//            messageLabel.setText("Login successful! Welcome, " + user.getName());
//
//            authSession.setUser(user);
//
//            mainApp.getInstance().showMainLayout();
//        } else {
//            messageLabel.setText("Invalid email or password");
//        }
//    }

    @FXML
    private void goLandingPage() {
        mainApp.getInstance().showLandingPage();
    }
}
