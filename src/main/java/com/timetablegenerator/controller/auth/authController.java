package com.timetablegenerator.controller.auth;

import com.timetablegenerator.mainApp;
import com.timetablegenerator.model.userModel;
import com.timetablegenerator.service.authService;
import com.timetablegenerator.util.authSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class authController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final authService authService = new authService();

    @FXML
    private void login() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Email and password are required");
            return;
        }

        userModel user = authService.login(email, password);

        if (user != null) {
            authSession.setUser(user);

            if (user.isChange_password()) {
                mainApp.getInstance().showChangePassword();
                return;
            }

            handleRoleRedirection(user);

        } else {
            messageLabel.setText("Invalid email or password");
        }
    }

    private void handleRoleRedirection(userModel user) {
        switch (user.getRole()) {
            case admin:
                mainApp.getInstance().showMainLayout();
                break;
            case teacher:
                mainApp.getInstance().showTeacherLayout();
                break;
            default:
                messageLabel.setText("User role not recognized.");
                break;
        }
    }

    @FXML
    private void goLandingPage() {
        mainApp.getInstance().showLandingPage();
    }
}
