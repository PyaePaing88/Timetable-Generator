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
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.Timestamp;

public class courseEditController {

    @FXML
    private TextField nameField, subjectCodeField;
    @FXML
    private ComboBox<departmentModel> deptComboBox;
    @FXML
    private ComboBox<academicLevelModel> academicLevelComboBox; // Added Level ComboBox

    private final courseService service;
    private final departmentService deptService;
    private final academicLevelService levelService; // Added Level Service

    private courseModel currentCourse;

    public courseEditController() {
        this.service = new courseService(new courseRepo());
        this.deptService = new departmentService(new departmentRepo());
        this.levelService = new academicLevelService(new academicLevelRepo());
    }

    @FXML
    public void initialize() {
        try {
            deptComboBox.setItems(FXCollections.observableArrayList(deptService.getMinorDepartments()));
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

            academicLevelComboBox.setItems(FXCollections.observableArrayList(levelService.getAcademicLevelForCombo()));
            academicLevelComboBox.setConverter(new StringConverter<academicLevelModel>() {
                @Override
                public String toString(academicLevelModel level) {
                    return (level == null) ? "" : level.getYear();
                }

                @Override
                public academicLevelModel fromString(String string) {
                    return null;
                }
            });

        } catch (Exception e) {
            showAlert("Error", "Could not load data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void loadCourseData(int id) {
        try {
            this.currentCourse = service.getCourseById(id);
            if (currentCourse != null) {
                nameField.setText(currentCourse.getCourse_name());
                subjectCodeField.setText(currentCourse.getSubject_code());

                for (departmentModel dept : deptComboBox.getItems()) {
                    if (dept.getId() == currentCourse.getDepartment_id()) {
                        deptComboBox.setValue(dept);
                        break;
                    }
                }

                for (academicLevelModel level : academicLevelComboBox.getItems()) {
                    if (level.getId() == currentCourse.getAcademicLevel_id()) {
                        academicLevelComboBox.setValue(level);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            showAlert("Error", "Could not load course data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            try {
                currentCourse.setCourse_name(nameField.getText().trim());
                currentCourse.setSubject_code(subjectCodeField.getText().trim());

                departmentModel selectedDept = deptComboBox.getValue();
                if (selectedDept != null) {
                    currentCourse.setDepartment_id(selectedDept.getId());
                }

                academicLevelModel selectedLevel = academicLevelComboBox.getValue();
                if (selectedLevel != null) {
                    currentCourse.setAcademicLevel_id(selectedLevel.getId());
                }

                currentCourse.setModify_by(1);
                currentCourse.setModify_date(new Timestamp(System.currentTimeMillis()));

                service.saveCourses(currentCourse);

                showAlert("Success", "Course updated successfully!", Alert.AlertType.INFORMATION);
                closeWindow();
            } catch (Exception e) {
                showAlert("Database Error", "Update failed: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validateInput() {
        StringBuilder sb = new StringBuilder();
        if (nameField.getText().trim().isEmpty()) sb.append("- Course Name cannot be empty!\n");
        if (subjectCodeField.getText().trim().isEmpty()) sb.append("- Subject Code cannot be empty!\n");
        if (deptComboBox.getValue() == null) sb.append("- Please select a department.\n");
        if (academicLevelComboBox.getValue() == null) sb.append("- Please select an academic level.\n");

        if (sb.length() > 0) {
            showAlert("Validation Error", sb.toString(), Alert.AlertType.WARNING);
            return false;
        }
        return true;
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