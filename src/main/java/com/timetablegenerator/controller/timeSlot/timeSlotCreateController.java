package com.timetablegenerator.controller.timeSlot;

import com.timetablegenerator.model.day;
import com.timetablegenerator.model.timeSlotModel;
import com.timetablegenerator.repository.timeSlotRepo;
import com.timetablegenerator.service.timeSlotService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class timeSlotCreateController {

    @FXML
    private ComboBox<day> dayComboBox;
    @FXML
    private Spinner<Integer> periodSpinner, startHour, startMin, endHour, endMin;

    @FXML
    private RadioButton morningRadio;
    @FXML
    private RadioButton eveningRadio;
    private ToggleGroup shiftGroup;

    private final timeSlotService service;

    public timeSlotCreateController() {
        this.service = new timeSlotService(new timeSlotRepo());
    }

    @FXML
    public void initialize() {
        dayComboBox.setItems(FXCollections.observableArrayList(day.values()));

        periodSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6, 1));

        startHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 12, 9));
        endHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 12, 10));
        startMin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        endMin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        shiftGroup = new ToggleGroup();
        morningRadio.setToggleGroup(shiftGroup);
        eveningRadio.setToggleGroup(shiftGroup);
        morningRadio.setSelected(true);
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            try {
                timeSlotModel newSlot = new timeSlotModel();
                newSlot.setPeriod(periodSpinner.getValue());
                newSlot.setDay_of_week(dayComboBox.getValue());

                // Set shift boolean: Morning = true, Evening = false
                newSlot.setIs_morning_shift(morningRadio.isSelected());

                java.time.LocalTime start = java.time.LocalTime.of(startHour.getValue(), startMin.getValue());
                java.time.LocalTime end = java.time.LocalTime.of(endHour.getValue(), endMin.getValue());

                newSlot.setStart_time(java.sql.Time.valueOf(start));
                newSlot.setEnd_time(java.sql.Time.valueOf(end));

                newSlot.setIs_delete(false);

                service.saveTimeSlot(newSlot);
                showAlert("Success", "Time Slot saved!", Alert.AlertType.INFORMATION);
                closeWindow();
            } catch (Exception e) {
                showAlert("Error", "Error: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validateInput() {
        StringBuilder errorMsg = new StringBuilder();

        if (dayComboBox.getValue() == null) {
            errorMsg.append("Day of week is required.\n");
        }

        // Ensure a shift is selected
        if (shiftGroup.getSelectedToggle() == null) {
            errorMsg.append("Please select a shift.\n");
        }

        int startTotal = (startHour.getValue() * 60) + startMin.getValue();
        int endTotal = (endHour.getValue() * 60) + endMin.getValue();

        if (endTotal <= startTotal) {
            errorMsg.append("End Time must be after Start Time.\n");
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
        Stage stage = (Stage) dayComboBox.getScene().getWindow();
        stage.close();
    }
}