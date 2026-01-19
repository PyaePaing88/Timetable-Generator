package com.timetablegenerator.controller.availability;

import com.timetablegenerator.model.availabilityModel;
import com.timetablegenerator.model.timeSlotModel;
import com.timetablegenerator.repository.timeSlotRepo;
import com.timetablegenerator.service.availabilityService;
import com.timetablegenerator.repository.availabilityRepo;
import com.timetablegenerator.util.authSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import jfxtras.scene.control.LocalDateTimeTextField;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class availabilityCreateController {

    @FXML
    private TextField statusField;
    @FXML
    private TextArea remarkArea;
    @FXML
    private ComboBox<String> dayComboBox;
    @FXML
    private ComboBox<timeSlotModel> periodComboBox;

    private final availabilityService service = new availabilityService(new availabilityRepo());
    private final timeSlotRepo timeSlot = new timeSlotRepo();

    private List<timeSlotModel> allTimeSlots = new ArrayList<>();

    @FXML
    public void initialize() {
        setupComboBoxes();
        loadData();
    }

    private void setupComboBoxes() {
        dayComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filterPeriodsByDay(newVal);
                periodComboBox.setDisable(false);
            }
        });

        periodComboBox.setConverter(new StringConverter<timeSlotModel>() {
            @Override
            public String toString(timeSlotModel slot) {
                return slot == null ? "" : "Period " + slot.getPeriod() + " (" + slot.getStart_time() + " - " + slot.getEnd_time() + ")";
            }

            @Override
            public timeSlotModel fromString(String string) {
                return null;
            }
        });
    }

    private void loadData() {
        try {
            allTimeSlots = timeSlot.findAllForCombo();

            List<String> uniqueDays = allTimeSlots.stream()
                    .map(slot -> slot.getDay_of_week().name())
                    .distinct()
                    .toList();

            dayComboBox.getItems().setAll(uniqueDays);
        } catch (Exception e) {
            showAlert("Error", "Failed to load time slots: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void filterPeriodsByDay(String selectedDayName) {
        List<timeSlotModel> filtered = allTimeSlots.stream()
                .filter(slot -> slot.getDay_of_week().name().equalsIgnoreCase(selectedDayName))
                .sorted(Comparator.comparingInt(timeSlotModel::getPeriod))
                .toList();

        periodComboBox.getItems().setAll(filtered);
        periodComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        try {
            availabilityModel model = new availabilityModel();
            model.setStatus(statusField.getText().trim());
            model.setRemark(remarkArea.getText().trim());

            timeSlotModel selectedSlot = periodComboBox.getValue();
            model.setTime_slot_id(selectedSlot.getId());

            service.saveAvailability(model);
            showAlert("Success", "Availability saved!", Alert.AlertType.INFORMATION);
            closeWindow();
        } catch (Exception e) {
            showAlert("Error", "Save failed: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validateInput() {
        if (statusField.getText().isBlank()) return false;
        if (dayComboBox.getValue() == null) return false;
        if (periodComboBox.getValue() == null) return false;
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