package com.timetablegenerator.controller.academicLevel;

import com.timetablegenerator.model.academicLevelModel;
import com.timetablegenerator.model.departmentModel;
import com.timetablegenerator.repository.academicLevelRepo;
import com.timetablegenerator.repository.departmentRepo;
import com.timetablegenerator.service.academicLevelService;
import com.timetablegenerator.service.departmentService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Timestamp;

public class academicLevelCreateController {
    @FXML private TextField nameField;


    private academicLevelService service;

    public academicLevelCreateController() {
        this.service = new academicLevelService(new academicLevelRepo());
    }

    @FXML
    public void initialize() {
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            try {
                academicLevelModel newAl = new academicLevelModel();

                newAl.setYear(nameField.getText().trim());
                newAl.setIs_delete(false);

                newAl.setCreated_by(1);
                newAl.setCreated_date(new Timestamp(System.currentTimeMillis()));

                service.saveAcademicLevel(newAl);

                showAlert("Success", "Academic Level created successfully!", Alert.AlertType.INFORMATION);
                closeWindow();

            } catch (Exception e) {
                showAlert("Database Error", "Failed to create Academic Level: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validateInput() {
        String errorMsg = "";

        if (nameField.getText().isEmpty()) errorMsg += "Year is required.\n";

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
