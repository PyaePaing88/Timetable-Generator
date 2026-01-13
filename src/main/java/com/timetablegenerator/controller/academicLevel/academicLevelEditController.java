package com.timetablegenerator.controller.academicLevel;

import com.timetablegenerator.model.academicLevelModel;
import com.timetablegenerator.repository.academicLevelRepo;
import com.timetablegenerator.service.academicLevelService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Timestamp;

public class academicLevelEditController {

    @FXML private TextField nameField;

    private final academicLevelService service;
    private academicLevelModel currentAl;

    public academicLevelEditController() {
        this.service = new academicLevelService(new academicLevelRepo());
    }

    @FXML
    public void initialize() {
    }

    public void loadAcademicLevel(int deptId) {
        try {
            this.currentAl = service.getAcademicLevelById(deptId);
            if (currentAl != null) {
                nameField.setText(currentAl.getYear());
            }
        } catch (Exception e) {
            showAlert("Error", "Could not load uepartment data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            try {
                currentAl.setYear(nameField.getText());
                currentAl.setModify_by(1);
                currentAl.setModify_date(new Timestamp(System.currentTimeMillis()));

                service.saveAcademicLevel(currentAl);

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
