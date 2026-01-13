package com.timetablegenerator.controller.department;

import com.timetablegenerator.model.departmentModel;
import com.timetablegenerator.repository.departmentRepo;
import com.timetablegenerator.service.departmentService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.Timestamp;

public class departmentCreateController {

    @FXML private TextField nameField;
    @FXML private CheckBox minorCheckBox;

    private final departmentService service;

    public departmentCreateController() {
        this.service = new departmentService(new departmentRepo());
    }

    @FXML
    public void initialize() {
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            try {
                departmentModel newDept = new departmentModel();

                newDept.setDepartment_name(nameField.getText().trim());
                newDept.setIs_minor(minorCheckBox.isSelected());
                newDept.setIs_delete(false);

                newDept.setCreated_by(1);
                newDept.setCreated_date(new Timestamp(System.currentTimeMillis()));

                service.saveDepartment(newDept);

                showAlert("Success", "Department created successfully!", Alert.AlertType.INFORMATION);
                closeWindow();

            } catch (Exception e) {
                showAlert("Database Error", "Failed to create department: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validateInput() {
        String errorMsg = "";

        if (nameField.getText().isEmpty()) errorMsg += "Name is required.\n";

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
