package com.timetablegenerator.controller.timetable;

import com.timetablegenerator.model.departmentModel;
import com.timetablegenerator.repository.departmentRepo;
import com.timetablegenerator.service.departmentService;
import com.timetablegenerator.service.timetableService;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class timetableCreateController {
    @FXML
    private ComboBox<departmentModel> departmentComboBox;
    @FXML
    private Button generateBtn;
    @FXML
    private ProgressIndicator loadingIndicator;

    private final timetableService service;
    private final departmentService deptService;

    public timetableCreateController() {
        this.service = new timetableService();
        this.deptService = new departmentService(new departmentRepo());
    }

    @FXML
    public void initialize() {
        setupDepartmentComboBox();
        loadingIndicator.setVisible(false);
    }

    private void setupDepartmentComboBox() {
        try {
            departmentComboBox.setItems(FXCollections.observableArrayList(deptService.getMajorDepartments()));

            departmentComboBox.setConverter(new StringConverter<departmentModel>() {
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
            showAlert("Error", "Failed to load departments: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleGenerate() {
        if (!validateInput()) return;

        departmentModel selectedDept = departmentComboBox.getSelectionModel().getSelectedItem();

        toggleUI(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                service.generateWeeklyTimetable(selectedDept.getId());
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            toggleUI(false);
            showAlert("Success", "Timetable generated successfully for all classes in "
                    + selectedDept.getDepartment_name() + "!", Alert.AlertType.INFORMATION);
            closeWindow();
        });

        task.setOnFailed(e -> {
            toggleUI(false);
            Throwable ex = task.getException();
            showAlert("Generation Failed", "Could not satisfy constraints or DB error: " + ex.getMessage(), Alert.AlertType.ERROR);
            ex.printStackTrace();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private boolean validateInput() {
        if (departmentComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select a department before generating.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void toggleUI(boolean isProcessing) {
        generateBtn.setDisable(isProcessing);
        departmentComboBox.setDisable(isProcessing);
        loadingIndicator.setVisible(isProcessing);
        generateBtn.setText(isProcessing ? "Generating..." : "Generate Timetable");
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

        try {
            DialogPane dp = alert.getDialogPane();
            String css = getClass().getResource("/com/timetablegenerator/style/styles.css").toExternalForm();
            dp.getStylesheets().add(css);
            dp.getStyleClass().add("my-alert");
        } catch (Exception ignored) {
        }

        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) generateBtn.getScene().getWindow();
        stage.close();
    }
}