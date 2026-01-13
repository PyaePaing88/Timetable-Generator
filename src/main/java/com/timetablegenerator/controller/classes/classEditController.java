package com.timetablegenerator.controller.classes;

import com.timetablegenerator.model.classModel;
import com.timetablegenerator.model.departmentModel;
import com.timetablegenerator.repository.classRepo;
import com.timetablegenerator.repository.departmentRepo;
import com.timetablegenerator.service.classService;
import com.timetablegenerator.service.departmentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.Timestamp;

public class classEditController {

    @FXML private TextField nameField;
    @FXML private ComboBox<departmentModel> deptComboBox;

    private final classService service;
    private final departmentService deptService;
    private classModel currentClass;

    public classEditController() {
        this.service = new classService(new classRepo());
        this.deptService = new departmentService(new departmentRepo());
    }

    @FXML
    public void initialize() {
        try {
            deptComboBox.setItems(FXCollections.observableArrayList(deptService.getMajorDepartments()));

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
        }
    }

    public void loadClassData(int id) {
        try {
            this.currentClass = service.getClassById(id);
            if (currentClass != null) {
                nameField.setText(currentClass.getClass_name());

                // FIX 2: Auto-select the correct department in the ComboBox
                for (departmentModel dept : deptComboBox.getItems()) {
                    if (dept.getId() == currentClass.getDepartment_id()) {
                        deptComboBox.setValue(dept);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            showAlert("Error", "Could not load class data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            try {
                currentClass.setClass_name(nameField.getText());
                departmentModel selectedDept = deptComboBox.getValue();
                if (selectedDept != null) {
                    currentClass.setDepartment_id(selectedDept.getId());
                }
                currentClass.setModify_by(1);
                currentClass.setModify_date(new Timestamp(System.currentTimeMillis()));

                service.saveClass(currentClass);

                showAlert("Success", "Class updated successfully!", Alert.AlertType.INFORMATION);
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
            showAlert("Validation Error", "Class Name cannot be empty!", Alert.AlertType.WARNING);
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
