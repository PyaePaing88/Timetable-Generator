package com.timetablegenerator.controller.availability;

import com.timetablegenerator.model.availabilityModel;
import com.timetablegenerator.model.timeSlotModel; // Assuming this is your model name
import com.timetablegenerator.repository.availabilityRepo;
import com.timetablegenerator.repository.timeSlotRepo; // Assuming this exists
import com.timetablegenerator.service.availabilityService;
import com.timetablegenerator.util.authSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class availabilityEditController {

    @FXML
    private TextField statusField;
    @FXML
    private TextArea remarkArea;
    @FXML
    private ComboBox<String> dayComboBox;
    @FXML
    private ComboBox<timeSlotModel> periodComboBox;

    private final availabilityService service = new availabilityService(new availabilityRepo());
    private final timeSlotRepo tsRepo = new timeSlotRepo();

    private availabilityModel currentAvailability;
    private List<timeSlotModel> allTimeSlots = new ArrayList<>();

    @FXML
    public void initialize() {
        setupComboBoxes();
        loadAllTimeSlots();
    }

    private void setupComboBoxes() {
        // Filter periods when day is selected
        dayComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filterPeriodsByDay(newVal);
                periodComboBox.setDisable(false);
            }
        });

        // Display "Period X (Start - End)" in the ComboBox
        periodComboBox.setConverter(new StringConverter<timeSlotModel>() {
            @Override
            public String toString(timeSlotModel slot) {
                return slot == null ? "" : "Period " + slot.getPeriod();
            }

            @Override
            public timeSlotModel fromString(String string) {
                return null;
            }
        });
    }

    private void loadAllTimeSlots() {
        try {
            allTimeSlots = tsRepo.findAllForCombo();
            List<String> uniqueDays = allTimeSlots.stream()
                    .map(slot -> slot.getDay_of_week().name())
                    .distinct()
                    .toList();
            dayComboBox.getItems().setAll(uniqueDays);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAvailabilityData(int id) {
        try {
            this.currentAvailability = service.getAvailabilityById(id);
            if (currentAvailability != null) {
                statusField.setText(currentAvailability.getStatus());
                remarkArea.setText(currentAvailability.getRemark());

                // 1. Find the specific time slot object from our list
                timeSlotModel selectedSlot = allTimeSlots.stream()
                        .filter(ts -> ts.getId() == currentAvailability.getTime_slot_id())
                        .findFirst()
                        .orElse(null);

                if (selectedSlot != null) {
                    // 2. Set the Day first (this triggers the listener to fill periodComboBox)
                    dayComboBox.getSelectionModel().select(selectedSlot.getDay_of_week().name());

                    // 3. Set the Period
                    periodComboBox.getSelectionModel().select(selectedSlot);
                }
            }
        } catch (Exception e) {
            showAlert("Error", "Could not load data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void filterPeriodsByDay(String dayName) {
        List<timeSlotModel> filtered = allTimeSlots.stream()
                .filter(ts -> ts.getDay_of_week().name().equalsIgnoreCase(dayName))
                .sorted(Comparator.comparingInt(timeSlotModel::getPeriod))
                .toList();
        periodComboBox.getItems().setAll(filtered);
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) return;

        try {
            currentAvailability.setStatus(statusField.getText().trim());
            currentAvailability.setRemark(remarkArea.getText().trim());

            // Link the selected time slot ID
            timeSlotModel selectedSlot = periodComboBox.getValue();
            currentAvailability.setTime_slot_id(selectedSlot.getId());

            if (authSession.getUser() != null) {
                currentAvailability.setModify_by(authSession.getUser().getId());
            }

            service.saveAvailability(currentAvailability);
            showAlert("Success", "Updated successfully!", Alert.AlertType.INFORMATION);
            closeWindow();
        } catch (Exception e) {
            showAlert("Database Error", "Update failed: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validateInput() {
        if (statusField.getText().isBlank()) return false;
        if (dayComboBox.getValue() == null) return false;
        if (periodComboBox.getValue() == null) return false;
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