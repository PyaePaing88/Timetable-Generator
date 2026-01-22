package com.timetablegenerator.controller.auth;

import com.timetablegenerator.mainApp;
import com.timetablegenerator.model.userModel;
import com.timetablegenerator.service.authService;
import com.timetablegenerator.util.authSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class changePasswordController {
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label messageLabel;

    private final authService Service = new authService();

    @FXML
    private void updatePassword() {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        userModel currentUser = authSession.getUser();

        if (newPassword.isEmpty() || !newPassword.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match or are empty.");
            return;
        }

        boolean success = Service.changePassword(currentUser.getId(), newPassword);

        if (success) {
            currentUser.setChange_password(false);

            if (currentUser.getRole().name().equals("admin")) {
                mainApp.getInstance().showMainLayout();
            } else {
                mainApp.getInstance().showTeacherLayout();
            }
        } else {
            messageLabel.setText("Error updating password. Try again.");
        }
    }

    @FXML
    private void goLandingPage() {
        mainApp.getInstance().showLandingPage();
    }
}
