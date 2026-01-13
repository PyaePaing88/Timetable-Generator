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
    private TextField periodField;
    @FXML
    private Spinner<Integer> startHour, startMin, endHour, endMin;

    private final timeSlotService service;
    private int timeSlotId;

    public timeSlotEditController() {
        this.service = new timeSlotService(new timeSlotRepo());
    }

    @FXML
    public void initialize() {
        dayComboBox.setItems(FXCollections.observableArrayList(day.values()));

        startHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        endHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        startMin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        endMin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
    }

    public void loadTimeSlotData(int id) {
        this.timeSlotId = id;
        try {
            timeSlotModel slot = service.getTimeSlotById(id);
            if (slot != null) {
                dayComboBox.setValue(slot.getDay_of_week());
                periodField.setText(String.valueOf(slot.getPeriod()));

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
                slot.setPeriod(Integer.parseInt(periodField.getText().trim()));

                LocalTime start = LocalTime.of(startHour.getValue(), startMin.getValue());
                LocalTime end = LocalTime.of(endHour.getValue(), endMin.getValue());

                slot.setStart_time(Time.valueOf(start));
                slot.setEnd_time(Time.valueOf(end));
                slot.setIs_delete(false);

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
        int startTotal = (startHour.getValue() * 60) + startMin.getValue();
        int endTotal = (endHour.getValue() * 60) + endMin.getValue();

        try {
            if (dayComboBox.getValue() == null || periodField.getText().trim().isEmpty()) {
                showAlert("Validation Error", "All fields are required.", Alert.AlertType.WARNING);
                return false;
            }
            Integer.parseInt(periodField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Period must be a number.", Alert.AlertType.WARNING);
            return false;
        }

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