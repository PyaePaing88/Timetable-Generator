package com.timetablegenerator.controller.user;

import com.timetablegenerator.model.role;
import com.timetablegenerator.model.userModel;
import com.timetablegenerator.repository.userRepo;
import com.timetablegenerator.service.userService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.Timestamp;

public class userCreateController {

    @FXML private TextField nameField, emailField, phoneField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox activeCheckBox, changePassCheckBox;
    @FXML private ComboBox<role> roleComboBox;

    private userService service;

    public userCreateController() {
        this.service = new userService(new userRepo());
    }

    @FXML
    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList(role.values()));
        roleComboBox.setPromptText("Select Role");
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            try {
                userModel newUser = new userModel();

                newUser.setName(nameField.getText().trim());
                newUser.setEmail(emailField.getText().trim());
                newUser.setPhone(phoneField.getText().trim());
                newUser.setPassword(passwordField.getText());
                newUser.setRole(roleComboBox.getValue());
                newUser.setIs_active(activeCheckBox.isSelected());
                newUser.setChange_password(changePassCheckBox.isSelected());
                newUser.setIs_delete(false);

                newUser.setCreated_by(1);
                newUser.setCreated_date(new Timestamp(System.currentTimeMillis()));

                service.saveUser(newUser);

                showAlert("Success", "User created successfully!", Alert.AlertType.INFORMATION);
                closeWindow();

            } catch (NumberFormatException e) {
                showAlert("Error", "Department ID must be a valid number.", Alert.AlertType.ERROR);
            } catch (Exception e) {
                showAlert("Database Error", "Failed to create user: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validateInput() {
        String errorMsg = "";

        if (nameField.getText().isEmpty()) errorMsg += "Name is required.\n";
        if (emailField.getText().isEmpty() || !emailField.getText().contains("@")) errorMsg += "Valid email is required.\n";
        if (passwordField.getText().length() < 6) errorMsg += "Password must be at least 6 characters.\n";
        if (roleComboBox.getValue() == null) errorMsg += "Role must be selected.\n";

        if (!errorMsg.isEmpty()) {
            showAlert("Validation Error", errorMsg, Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    @FXML private void handleCancel() { closeWindow(); }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}