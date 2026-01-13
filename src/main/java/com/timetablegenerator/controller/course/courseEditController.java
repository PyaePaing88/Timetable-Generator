package com.timetablegenerator.controller.course;

import com.timetablegenerator.model.courseModel;
import com.timetablegenerator.model.departmentModel;
import com.timetablegenerator.repository.courseRepo;
import com.timetablegenerator.repository.departmentRepo;
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

    @FXML private TextField nameField;
    @FXML private ComboBox<departmentModel> deptComboBox;

    private final courseService service;
    private final departmentService deptService;

    private courseModel currentCourse;

    public courseEditController() {
        this.service = new courseService(new courseRepo());
        this.deptService = new departmentService(new departmentRepo());
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

        } catch (Exception e) {
            showAlert("Error", "Could not load departments: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void loadCourseData(int id) {
        try {
            this.currentCourse = service.getCourseById(id);
            if (currentCourse != null) {
                nameField.setText(currentCourse.getCourse_name());

                for (departmentModel dept : deptComboBox.getItems()) {
                    if (dept.getId() == currentCourse.getDepartment_id()) {
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
                currentCourse.setCourse_name(nameField.getText());
                departmentModel selectedDept = deptComboBox.getValue();
                if (selectedDept != null) {
                    currentCourse.setDepartment_id(selectedDept.getId());
                }
                currentCourse.setModify_by(1);
                currentCourse.setModify_date(new Timestamp(System.currentTimeMillis()));

                service.saveCourses(currentCourse);

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
