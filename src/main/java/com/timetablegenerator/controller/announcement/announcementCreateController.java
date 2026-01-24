package com.timetablegenerator.controller.announcement;

import com.timetablegenerator.model.announcementModel;
import com.timetablegenerator.model.announcementType;
import com.timetablegenerator.model.role;
import com.timetablegenerator.model.userModel;
import com.timetablegenerator.repository.announcementRepo;
import com.timetablegenerator.service.announcementService;
import com.timetablegenerator.service.userService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Timestamp;

public class announcementCreateController {

    @FXML
    private TextField titleField;
    @FXML
    private TextArea subjectArea;
    @FXML
    private ComboBox<announcementType> typeCombo;

    private final announcementService service;

    public announcementCreateController() {
        this.service = new announcementService(new announcementRepo());
    }

    @FXML
    public void initialize() {
        typeCombo.setItems(FXCollections.observableArrayList(announcementType.values()));
        typeCombo.setPromptText("Select Type");
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            try {
                announcementModel newAnnouncement = new announcementModel();

                newAnnouncement.setTitle(titleField.getText().trim());
                newAnnouncement.setMessage(subjectArea.getText().trim());
                newAnnouncement.setType(typeCombo.getValue());
                newAnnouncement.setIs_delete(false);

                newAnnouncement.setCreated_by(1);
                newAnnouncement.setCreated_date(new Timestamp(System.currentTimeMillis()));

                service.saveAnnouncement(newAnnouncement);

                showAlert("Success", "Create Success!", Alert.AlertType.INFORMATION);
                closeWindow();

            } catch (Exception e) {
                showAlert("Database Error", "Failed to create announcement: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validateInput() {
        String errorMsg = "";

        if (titleField.getText().isEmpty()) errorMsg += "Title is required.\n";
        if (subjectArea.getText().isEmpty()) errorMsg += "Subject is required.\n";
        if (typeCombo.getValue() == null) errorMsg += "Type must be selected.\n";

        if (!errorMsg.isEmpty()) {
            showAlert("Validation Error", errorMsg, Alert.AlertType.WARNING);
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
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
}
