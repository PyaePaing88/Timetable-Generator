package com.timetablegenerator.controller.course;

import com.timetablegenerator.model.academicLevelModel;
import com.timetablegenerator.model.courseModel;
import com.timetablegenerator.model.departmentModel;
import com.timetablegenerator.repository.academicLevelRepo;
import com.timetablegenerator.repository.courseRepo;
import com.timetablegenerator.repository.departmentRepo;
import com.timetablegenerator.service.academicLevelService;
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

    @FXML
    private TextField nameField, subjectCodeField;
    @FXML
    private ComboBox<departmentModel> deptComboBox;
    @FXML
    private ComboBox<academicLevelModel> academicLevelComboBox;

    private final courseService service;
    private final departmentService deptService;
    private final academicLevelService levelService; // Renamed for clarity

    public courseCreateController() {
        this.service = new courseService(new courseRepo());
        this.deptService = new departmentService(new departmentRepo());
        this.levelService = new academicLevelService(new academicLevelRepo());
    }

    @FXML
    public void initialize() {
        try {
            // Load and Setup Department ComboBox
            deptComboBox.setItems(FXCollections.observableArrayList(deptService.getMinorDepartments()));
            deptComboBox.setConverter(createDeptConverter());

            // Load and Setup Academic Level ComboBox
            academicLevelComboBox.setItems(FXCollections.observableArrayList(levelService.getAcademicLevelForCombo()));
            academicLevelComboBox.setConverter(createLevelConverter());

        } catch (Exception e) {
            showAlert("Error", "Could not load initial data: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            try {
                courseModel newcourse = new courseModel();
                departmentModel selectedDept = deptComboBox.getSelectionModel().getSelectedItem();
                academicLevelModel selectedLevel = academicLevelComboBox.getSelectionModel().getSelectedItem();

                newcourse.setCourse_name(nameField.getText().trim());
                newcourse.setSubject_code(subjectCodeField.getText().trim());
                newcourse.setDepartment_id(selectedDept.getId());
                newcourse.setAcademicLevel_id(selectedLevel.getId()); // Connects the course to the level
                newcourse.setIs_delete(false);
                newcourse.setCreated_by(1); // Set current user ID here
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
        StringBuilder errorMsg = new StringBuilder();

        if (nameField.getText().trim().isEmpty()) errorMsg.append("Course Name is required.\n");
        if (subjectCodeField.getText().trim().isEmpty()) errorMsg.append("Subject Code is required.\n");
        if (deptComboBox.getSelectionModel().getSelectedItem() == null)
            errorMsg.append("Please select a department.\n");
        if (academicLevelComboBox.getSelectionModel().getSelectedItem() == null)
            errorMsg.append("Please select an academic level.\n");

        if (errorMsg.length() > 0) {
            showAlert("Validation Error", errorMsg.toString(), Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }
    
    private StringConverter<departmentModel> createDeptConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(departmentModel dept) {
                return (dept == null) ? "" : dept.getDepartment_name();
            }

            @Override
            public departmentModel fromString(String string) {
                return null;
            }
        };
    }

    private StringConverter<academicLevelModel> createLevelConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(academicLevelModel level) {
                return (level == null) ? "" : level.getYear();
            }

            @Override
            public academicLevelModel fromString(String string) {
                return null;
            }
        };
    }

    @FXML
    private void handleCancel() {
        closeWindow();
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