package com.timetablegenerator.controller.classes;

import com.timetablegenerator.model.academicLevelModel;
import com.timetablegenerator.model.classModel;
import com.timetablegenerator.model.departmentModel;
import com.timetablegenerator.repository.academicLevelRepo;
import com.timetablegenerator.repository.classRepo;
import com.timetablegenerator.repository.departmentRepo;
import com.timetablegenerator.service.academicLevelService;
import com.timetablegenerator.service.classService;
import com.timetablegenerator.service.departmentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class classGenerateController {
    @FXML private ComboBox<departmentModel> deptComboBox;
    @FXML private VBox academicLevelContainer;
    private final List<CheckBox> levelCheckBoxes = new ArrayList<>();
    private final classService service;
    private final departmentService deptService;
    private final academicLevelService levelService;

    public classGenerateController() {
        this.service = new classService(new classRepo());
        this.deptService = new departmentService(new departmentRepo());
        this.levelService = new academicLevelService(new academicLevelRepo());
    }

    @FXML
    public void initialize() {
        loadDepartments();
        loadAcademicLevels();
    }

    @FXML
    public void loadDepartments() {
        try {
            deptComboBox.setItems(FXCollections.observableArrayList(deptService.getMajorDepartments()));

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

    private void loadAcademicLevels() {
        try {
            List<academicLevelModel> levels = levelService.getAcademicLevelForCombo();
            for (academicLevelModel level : levels) {
                CheckBox cb = new CheckBox(level.getYear());
                cb.setUserData(level);
                academicLevelContainer.getChildren().add(cb);
                levelCheckBoxes.add(cb);
            }
        } catch (Exception e) {
            showAlert("Error", "Could not load levels: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleGenerate() {
        departmentModel selectedDept = deptComboBox.getSelectionModel().getSelectedItem();
        List<CheckBox> selectedLevels = levelCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .toList();

        if (selectedDept == null || selectedLevels.isEmpty()) {
            showAlert("Validation Error", "Please select a department and at least one level.", Alert.AlertType.WARNING);
            return;
        }

        List<String> duplicates = new ArrayList<>();
        int successCount = 0;

        for (CheckBox cb : selectedLevels) {
            academicLevelModel level = (academicLevelModel) cb.getUserData();
            String generatedName = level.getYear() + " " + selectedDept.getDepartment_name();

            try {
                classModel newClass = new classModel();
                newClass.setClass_name(generatedName);
                newClass.setDepartment_id(selectedDept.getId());
                newClass.setIs_delete(false);
                newClass.setCreated_date(new Timestamp(System.currentTimeMillis()));

                service.saveClass(newClass);
                successCount++;

            } catch (Exception e) {
                duplicates.add(generatedName);
            }
        }

        if (duplicates.isEmpty()) {
            showAlert("Success", "All " + successCount + " classes generated successfully!", Alert.AlertType.INFORMATION);
            closeWindow();
        } else {
            StringBuilder msg = new StringBuilder();
            if (successCount > 0) {
                msg.append("Successfully created ").append(successCount).append(" classes.\n\n");
            }
            msg.append("The following records already exist and were skipped:\n");
            duplicates.forEach(d -> msg.append("- ").append(d).append("\n"));

            showAlert("Duplicate Records Found", msg.toString(), Alert.AlertType.WARNING);

            if (successCount > 0) closeWindow();
        }
    }
    private boolean validateInput() {
        String errorMsg = "";

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
        Stage stage = (Stage) deptComboBox.getScene().getWindow();
        stage.close();
    }
}
