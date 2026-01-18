package com.timetablegenerator.controller.timeSlot;

import com.timetablegenerator.model.day;
import com.timetablegenerator.model.timeSlotModel;
import com.timetablegenerator.repository.timeSlotRepo;
import com.timetablegenerator.service.timeSlotService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Time;
import java.time.LocalTime;

public class timeSlotEditController {

    @FXML
    private ComboBox<day> dayComboBox;
    @FXML
    private Spinner<Integer> periodSpinner;
    @FXML
    private Spinner<Integer> startHour, startMin, endHour, endMin;

    // --- New Shift Radio Components ---
    @FXML
    private RadioButton morningRadio;
    @FXML
    private RadioButton eveningRadio;
    private ToggleGroup shiftGroup;

    private final timeSlotService service;
    private int timeSlotId;

    public timeSlotEditController() {
        this.service = new timeSlotService(new timeSlotRepo());
    }

    @FXML
    public void initialize() {
        dayComboBox.setItems(FXCollections.observableArrayList(day.values()));

        periodSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));
        startHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 12, 0));
        endHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 12, 0));
        startMin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        endMin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        // Initialize ToggleGroup for Shift
        shiftGroup = new ToggleGroup();
        morningRadio.setToggleGroup(shiftGroup);
        eveningRadio.setToggleGroup(shiftGroup);
    }

    public void loadTimeSlotData(int id) {
        this.timeSlotId = id;
        try {
            timeSlotModel slot = service.getTimeSlotById(id);
            if (slot != null) {
                dayComboBox.setValue(slot.getDay_of_week());
                periodSpinner.getValueFactory().setValue(slot.getPeriod());

                // Set RadioButton selection based on shift data
                if (slot.isIs_morning_shift()) {
                    morningRadio.setSelected(true);
                } else {
                    eveningRadio.setSelected(true);
                }

                if (slot.getStart_time() != null) {
                    LocalTime startTime = slot.getStart_time().toLocalTime();
                    startHour.getValueFactory().setValue(startTime.getHour());
                    startMin.getValueFactory().setValue(startTime.getMinute());
                }

                if (slot.getEnd_time() != null) {
                    LocalTime endTime = slot.getEnd_time().toLocalTime();
                    endHour.getValueFactory().setValue(endTime.getHour());
                    endMin.getValueFactory().setValue(endTime.getMinute());
                }
            }
        } catch (Exception e) {
            showAlert("Error", "Could not load data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdate() {
        if (validateInput()) {
            try {
                timeSlotModel slot = new timeSlotModel();
                slot.setId(timeSlotId);
                slot.setDay_of_week(dayComboBox.getValue());
                slot.setPeriod(periodSpinner.getValue());

                // Get shift selection: Morning = true, Evening = false
                slot.setIs_morning_shift(morningRadio.isSelected());

                LocalTime start = LocalTime.of(startHour.getValue(), startMin.getValue());
                LocalTime end = LocalTime.of(endHour.getValue(), endMin.getValue());

                slot.setStart_time(Time.valueOf(start));
                slot.setEnd_time(Time.valueOf(end));
                slot.setIs_delete(false);

                // Ensure your service method handles update logic correctly
                service.saveTimeSlot(slot);
                showAlert("Success", "Time Slot updated successfully!", Alert.AlertType.INFORMATION);
                closeWindow();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Update failed: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validateInput() {
        if (dayComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select a Day of Week.", Alert.AlertType.WARNING);
            return false;
        }

        if (shiftGroup.getSelectedToggle() == null) {
            showAlert("Validation Error", "Please select a shift.", Alert.AlertType.WARNING);
            return false;
        }

        int startTotal = (startHour.getValue() * 60) + startMin.getValue();
        int endTotal = (endHour.getValue() * 60) + endMin.getValue();

        if (endTotal <= startTotal) {
            showAlert("Validation Error", "End time must be after start time.", Alert.AlertType.WARNING);
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