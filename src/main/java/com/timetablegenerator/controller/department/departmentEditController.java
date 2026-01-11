package com.timetablegenerator.controller.department;

import com.timetablegenerator.model.departmentModel;
import com.timetablegenerator.repository.departmentRepo;
import com.timetablegenerator.service.departmentService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Timestamp;

public class departmentEditController {

    @FXML
    private TextField nameField;

    private departmentService service;
    private departmentModel currentDepartment;

    public departmentEditController() {
        this.service = new departmentService(new departmentRepo());
    }

    @FXML
    public void initialize() {
    }

    public void loadDepartmentData(int deptId) {
        try {
            this.currentDepartment = service.getDepartmentById(deptId);
            if (currentDepartment != null) {
                nameField.setText(currentDepartment.getDepartment_name());
            }
        } catch (Exception e) {
            showAlert("Error", "Could not load uepartment data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            try {
                currentDepartment.setDepartment_name(nameField.getText());
                currentDepartment.setModify_by(1);
                currentDepartment.setModify_date(new Timestamp(System.currentTimeMillis()));

                service.saveDepartment(currentDepartment);

                showAlert("Success", "Department updated successfully!", Alert.AlertType.INFORMATION);
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
        if (nameField.getText().isEmpty() ) {
            showAlert("Validation Error", "Department Name cannot be empty!", Alert.AlertType.WARNING);
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
