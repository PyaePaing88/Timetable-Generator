package com.timetablegenerator.controller.timetable;

import com.timetablegenerator.model.TimetableDetailDTO;
import com.timetablegenerator.service.timetableService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class timetableEditController {

    @FXML
    private Label lblSlotInfo;
    @FXML
    private ComboBox<String> statusField; // Added Generic Type
    @FXML
    private TextArea remarkArea;

    private final timetableService service = new timetableService();
    private TimetableDetailDTO currentData;

    @FXML
    public void initialize() {
        // Populate the ComboBox with options
        statusField.setItems(FXCollections.observableArrayList("Active", "On Leave"));
    }

    public void initData(Integer id) throws SQLException {
        currentData = service.getTimetableAssignmentById(id);

        if (currentData != null) {
            lblSlotInfo.setText(currentData.getSubjectCode() + " - " + currentData.getDay() + " (Period " + currentData.getPeriod() + ")");
            statusField.setValue(currentData.isIs_leave() ? "On Leave" : "Active");
            remarkArea.setText(currentData.getRemark());
        }
    }

    @FXML
    private void handleSave() {
        try {
            currentData.setIs_leave(statusField.getValue().equals("On Leave"));
            currentData.setRemark(remarkArea.getText());

            // Now we can use the boolean result!
            boolean success = service.updateTimetableAssignment(currentData);

            if (success) {
                showAlert("Success", "Changes saved.", Alert.AlertType.INFORMATION);
                closeWindow();
            } else {
                showAlert("Error", "No records were updated.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            showAlert("Database Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) statusField.getScene().getWindow();
        if (stage != null) stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}