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

public class userEditController {

    @FXML private TextField nameField, emailField, phoneField;
    @FXML private CheckBox activeCheckBox, changePassCheckBox;
    @FXML private ComboBox<role> roleComboBox;

    private final userService service;
    private userModel currentUser;

    public userEditController() {
        this.service = new userService(new userRepo());
    }

    @FXML
    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList(role.values()));
    }

    public void loadUserData(int userId) {
        try {
            this.currentUser = service.getUserById(userId);
            if (currentUser != null) {
                nameField.setText(currentUser.getName());
                emailField.setText(currentUser.getEmail());
                phoneField.setText(currentUser.getPhone());
                roleComboBox.setValue(currentUser.getRole());
                activeCheckBox.setSelected(currentUser.isIs_active());
                changePassCheckBox.setSelected(currentUser.isChange_password());
            }
        } catch (Exception e) {
            showAlert("Error", "Could not load user data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            try {
                currentUser.setName(nameField.getText());
                currentUser.setEmail(emailField.getText());
                currentUser.setPhone(phoneField.getText());
                currentUser.setRole(roleComboBox.getValue());
                currentUser.setIs_active(activeCheckBox.isSelected());
                currentUser.setChange_password(changePassCheckBox.isSelected());

                currentUser.setModify_by(1);
                currentUser.setModify_date(new Timestamp(System.currentTimeMillis()));

                service.saveUser(currentUser);

                showAlert("Success", "User updated successfully!", Alert.AlertType.INFORMATION);
                closeWindow();
            } catch (Exception e) {
                showAlert("Database Error", "Update failed: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean validateInput() {
        if (nameField.getText().isEmpty() || emailField.getText().isEmpty()) {
            showAlert("Validation Error", "Name and Email cannot be empty!", Alert.AlertType.WARNING);
            return false;
        }
        if (!emailField.getText().contains("@")) {
            showAlert("Validation Error", "Please enter a valid email.", Alert.AlertType.WARNING);
            return false;
        }
        if (roleComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select a user role.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

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