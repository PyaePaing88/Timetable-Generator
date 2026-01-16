package com.timetablegenerator.controller.availability;

import com.timetablegenerator.model.availabilityModel;
import com.timetablegenerator.repository.availabilityRepo;
import com.timetablegenerator.service.availabilityService;
import com.timetablegenerator.util.authSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jfxtras.scene.control.LocalDateTimeTextField;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class availabilityEditController {

    @FXML
    private TextField statusField;
    @FXML
    private TextArea remarkArea;
    @FXML
    private LocalDateTimeTextField fromPicker;
    @FXML
    private LocalDateTimeTextField toPicker;

    private final availabilityService service;
    private availabilityModel currentAvailability;

    public availabilityEditController() {
        this.service = new availabilityService(new availabilityRepo());
    }

    @FXML
    public void initialize() {
    }

    public void loadAvailabilityData(int id) {
        try {
            this.currentAvailability = service.getAvailabilityById(id);
            if (currentAvailability != null) {
                statusField.setText(currentAvailability.getStatus());
                remarkArea.setText(currentAvailability.getRemark());

                // Convert Timestamp to LocalDateTime for JFXtras
                if (currentAvailability.getFrom() != null) {
                    fromPicker.setLocalDateTime(currentAvailability.getFrom().toLocalDateTime());
                }
                if (currentAvailability.getTo() != null) {
                    toPicker.setLocalDateTime(currentAvailability.getTo().toLocalDateTime());
                }
            }
        } catch (Exception e) {
            showAlert("Error", "Could not load availability data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        try {
            currentAvailability.setStatus(statusField.getText().trim());
            currentAvailability.setRemark(remarkArea.getText().trim());

            // Get LocalDateTime from pickers and convert to Timestamp
            currentAvailability.setFrom(Timestamp.valueOf(fromPicker.getLocalDateTime()));
            currentAvailability.setTo(Timestamp.valueOf(toPicker.getLocalDateTime()));

            if (authSession.getUser() != null) {
                currentAvailability.setModify_by(authSession.getUser().getId());
            }
            currentAvailability.setModify_date(new Timestamp(System.currentTimeMillis()));

            service.saveAvailability(currentAvailability);

            showAlert("Success", "Availability updated successfully!", Alert.AlertType.INFORMATION);
            closeWindow();
        } catch (Exception e) {
            showAlert("Database Error", "Update failed: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean validateInput() {
        StringBuilder errorMsg = new StringBuilder();

        if (statusField.getText().isBlank()) {
            errorMsg.append("- Status is required.\n");
        }

        LocalDateTime from = fromPicker.getLocalDateTime();
        LocalDateTime to = toPicker.getLocalDateTime();

        if (from == null || to == null) {
            errorMsg.append("- Both Start and End date/time are required.\n");
        } else if (to.isBefore(from)) {
            errorMsg.append("- End time cannot be earlier than start time.\n");
        }

        if (errorMsg.length() > 0) {
            showAlert("Validation Error", errorMsg.toString(), Alert.AlertType.WARNING);
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
        Stage stage = (Stage) statusField.getScene().getWindow();
        if (stage != null) stage.close();
    }
}