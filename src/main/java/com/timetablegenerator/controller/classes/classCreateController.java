package com.timetablegenerator.controller.classes;

import com.timetablegenerator.model.classModel;
import com.timetablegenerator.model.departmentModel;
import com.timetablegenerator.repository.classRepo;
import com.timetablegenerator.repository.departmentRepo;
import com.timetablegenerator.service.classService;
import com.timetablegenerator.service.departmentService;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Timestamp;

public class classCreateController {
    @FXML private TextField nameField;
    @FXML private ComboBox<departmentModel> deptComboBox;

    private classService service;
    private departmentService deptService;

    public classCreateController() {
        this.service = new classService(new classRepo());
        this.deptService = new departmentService(new departmentRepo());
    }

    @FXML
    public void initialize() {
        try {
            deptComboBox.setItems(FXCollections.observableArrayList(deptService.getDepartmentsForCombo()));

            deptComboBox.setPromptText("Select Department");

            deptComboBox.setConverter(new StringConverter<departmentModel>() {
                @Override
                public String toString(departmentModel dept) {
                    return (dept == null) ? "" : dept.getDepartment_name();
                }

                @Override
                public departmentModel fromString(String string) {
                    return null;
                }
            });

        } catch (Exception e) {
            showAlert("Error", "Could not load departments: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            try {
                classModel newclass = new classModel();

                departmentModel selectedDept = deptComboBox.getSelectionModel().getSelectedItem();

                newclass.setClass_name(nameField.getText().trim());
                newclass.setDepartment_id(selectedDept.getId());
                newclass.setIs_delete(false);

                newclass.setCreated_by(1);
                newclass.setCreated_date(new Timestamp(System.currentTimeMillis()));

                service.saveClass(newclass);

                showAlert("Success", "Class created successfully!", Alert.AlertType.INFORMATION);
                closeWindow();

            } catch (Exception e) {
                showAlert("Database Error", "Failed to create class: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validateInput() {
        String errorMsg = "";

        if (nameField.getText().isEmpty()) errorMsg += "Name is required.\n";

        if (deptComboBox.getSelectionModel().getSelectedItem() == null) {
            errorMsg += "Please select a department.\n";
        }

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
