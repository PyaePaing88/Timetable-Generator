package com.timetablegenerator.controller.timetable;

import com.timetablegenerator.model.TimetableDetailDTO;
import com.timetablegenerator.service.timetableService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class timetableEditController {

    @FXML
    private Label lblSlotInfo;
    @FXML
    private TextField statusField;
    @FXML
    private TextArea remarkArea;

    private final timetableService service = new timetableService();
    private TimetableDetailDTO currentData;

    public void initData(TimetableDetailDTO data) {
//        this.currentData = data;
//
//        lblSlotInfo.setText(data.getSubjectCode() + " - " + data.getDay() + " (Period " + data.getPeriod() + ")");
//
//        statusField.setText(data.getStatus());
//        remarkArea.setText(data.getRemark());
    }

//    @FXML
//    private void handleSave() {
//        if (currentData == null) return;
//
//        try {
//            currentData.setStatus(statusField.getText().trim());
//            currentData.setRemark(remarkArea.getText().trim());
//
//            service.updateSlotDetails(currentData);
//
//            showAlert("Success", "Schedule updated successfully!", Alert.AlertType.INFORMATION);
//            closeWindow();
//        } catch (SQLException e) {
//            showAlert("Error", "Failed to update: " + e.getMessage(), Alert.AlertType.ERROR);
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    private void handleCancel() {
//        closeWindow();
//    }
//
//    private void closeWindow() {
//        Stage stage = (Stage) statusField.getScene().getWindow();
//        if (stage != null) stage.close();
//    }
//
//    private void showAlert(String title, String content, Alert.AlertType type) {
//        Alert alert = new Alert(type);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(content);
//        alert.showAndWait();
//    }
}