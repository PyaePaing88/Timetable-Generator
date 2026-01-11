package com.timetablegenerator.controller.course;

import com.timetablegenerator.model.classModel;
import com.timetablegenerator.model.courseModel;
import com.timetablegenerator.model.departmentModel;
import com.timetablegenerator.repository.classRepo;
import com.timetablegenerator.repository.courseRepo;
import com.timetablegenerator.repository.departmentRepo;
import com.timetablegenerator.service.classService;
import com.timetablegenerator.service.courseService;
import com.timetablegenerator.service.departmentService;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Timestamp;

public class courseCreateController {
    @FXML private TextField nameField;
    @FXML private ComboBox<departmentModel> deptComboBox;

    private courseService service;
    private departmentService deptService;

    public courseCreateController() {
        this.service = new courseService(new courseRepo());
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
                courseModel newcourse = new courseModel();

                departmentModel selectedDept = deptComboBox.getSelectionModel().getSelectedItem();

                newcourse.setCourse_name(nameField.getText().trim());
                newcourse.setDepartment_id(selectedDept.getId());
                newcourse.setIs_delete(false);

                newcourse.setCreated_by(1);
                newcourse.setCreated_date(new Timestamp(System.currentTimeMillis()));

                service.saveCourses(newcourse);

                showAlert("Success", "Course created successfully!", Alert.AlertType.INFORMATION);
                closeWindow();

            } catch (Exception e) {
                showAlert("Database Error", "Failed to create course: " + e.getMessage(), Alert.AlertType.ERROR);
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
