package com.timetablegenerator.controller.availability;

import com.timetablegenerator.model.availabilityModel;
import com.timetablegenerator.service.availabilityService;
import com.timetablegenerator.repository.availabilityRepo;
import com.timetablegenerator.util.authSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jfxtras.scene.control.LocalDateTimeTextField;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class availabilityCreateController {

    @FXML
    private TextField statusField;
    @FXML
    private TextArea remarkArea;
    @FXML
    private LocalDateTimeTextField fromPicker;
    @FXML
    private LocalDateTimeTextField toPicker;

    private final availabilityService service;

    public availabilityCreateController() {
        this.service = new availabilityService(new availabilityRepo());
    }

    @FXML
    public void initialize() {
        // Set default values: From now until 1 hour from now
        fromPicker.setLocalDateTime(LocalDateTime.now());
        toPicker.setLocalDateTime(LocalDateTime.now().plusHours(1));
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        try {
            availabilityModel newAvailability = new availabilityModel();

            newAvailability.setStatus(statusField.getText().trim());
            newAvailability.setRemark(remarkArea.getText().trim());

            // Get values from JFXtras components
            LocalDateTime fromVal = fromPicker.getLocalDateTime();
            LocalDateTime toVal = toPicker.getLocalDateTime();

            newAvailability.setFrom(Timestamp.valueOf(fromVal));
            newAvailability.setTo(Timestamp.valueOf(toVal));
            newAvailability.setIs_delete(false);

            if (authSession.getUser() != null) {
                newAvailability.setCreated_by(authSession.getUser().getId());
            }

            service.saveAvailability(newAvailability);

            showAlert("Success", "Availability recorded successfully!", Alert.AlertType.INFORMATION);
            closeWindow();

        } catch (Exception e) {
            showAlert("Database Error", "Failed to save: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
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
        } else if (to.isEqual(from)) {
            errorMsg.append("- Start and End time cannot be exactly the same.\n");
        }

        if (errorMsg.length() > 0) {
            showAlert("Validation Error", errorMsg.toString(), Alert.AlertType.WARNING);
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
        Stage stage = (Stage) statusField.getScene().getWindow();
        if (stage != null) stage.close();
    }
}