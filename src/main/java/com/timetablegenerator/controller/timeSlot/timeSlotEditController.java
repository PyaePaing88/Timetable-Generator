package com.timetablegenerator.controller.timeSlot;

import com.timetablegenerator.model.day;
import com.timetablegenerator.model.timeSlotModel;
import com.timetablegenerator.repository.timeSlotRepo;
import com.timetablegenerator.service.timeSlotService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.Timestamp;
import java.util.Calendar;

public class timeSlotEditController {

    @FXML private ComboBox<day> dayComboBox;
    @FXML private TextField periodField;
    @FXML private Spinner<Integer> startHour, startMin, endHour, endMin;

    private final timeSlotService service;
    private int timeSlotId;

    public timeSlotEditController() {
        this.service = new timeSlotService(new timeSlotRepo());
    }

    @FXML
    public void initialize() {
        dayComboBox.setItems(FXCollections.observableArrayList(day.values()));

        // Initialize Spinners with 0-23 and 0-59 ranges
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

                // Convert Timestamps to Spinner Values
                setSpinnerTime(slot.getStart_time(), startHour, startMin);
                setSpinnerTime(slot.getEnd_time(), endHour, endMin);
            }
        } catch (Exception e) {
            showAlert("Error", "Could not load data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setSpinnerTime(Timestamp ts, Spinner<Integer> h, Spinner<Integer> m) {
        if (ts != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(ts.getTime());
            h.getValueFactory().setValue(cal.get(Calendar.HOUR_OF_DAY));
            m.getValueFactory().setValue(cal.get(Calendar.MINUTE));
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

                String startStr = String.format("1970-01-01 %02d:%02d:00", startHour.getValue(), startMin.getValue());
                String endStr = String.format("1970-01-01 %02d:%02d:00", endHour.getValue(), endMin.getValue());

                slot.setStart_time(Timestamp.valueOf(startStr));
                slot.setEnd_time(Timestamp.valueOf(endStr));
                slot.setIs_delete(false);

                service.saveTimeSlot(slot);
                showAlert("Success", "Time Slot updated successfully!", Alert.AlertType.INFORMATION);
                closeWindow();
            } catch (Exception e) {
                showAlert("Error", "Update failed: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validateInput() {
        int startTotal = (startHour.getValue() * 60) + startMin.getValue();
        int endTotal = (endHour.getValue() * 60) + endMin.getValue();

        if (dayComboBox.getValue() == null || periodField.getText().isEmpty()) {
            showAlert("Validation Error", "All fields are required.", Alert.AlertType.WARNING);
            return false;
        }
        if (endTotal <= startTotal) {
            showAlert("Validation Error", "End time must be after start time.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    @FXML private void handleCancel() { closeWindow(); }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) dayComboBox.getScene().getWindow();
        stage.close();
    }
}