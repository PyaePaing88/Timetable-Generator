package com.timetablegenerator.controller.availability;

import com.timetablegenerator.model.availabilityModel;
import com.timetablegenerator.repository.availabilityRepo;
import com.timetablegenerator.service.availabilityService;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class availabilityDetailController {

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtTeacher;
    @FXML
    private TextField txtStatus;
    // Changed from txtFrom/txtTo to Day/Period
    @FXML
    private TextField txtDay;
    @FXML
    private TextField txtPeriod;
    @FXML
    private TextArea txtRemark;

    private final availabilityService service = new availabilityService(new availabilityRepo());

    public void loadAvailabilityData(int id) {
        try {
            availabilityModel availability = service.getAvailabilityById(id);
            if (availability != null) {
                txtId.setText(String.valueOf(availability.getId()));
                txtTeacher.setText(availability.getTeacher_name());
                txtStatus.setText(availability.getStatus());

                // Display Day and Period from the model
                if (availability.getDay_of_week() != null) {
                    // Formats "MONDAY" to "Monday" for better readability
                    String dayName = availability.getDay_of_week().name().toLowerCase();
                    txtDay.setText(dayName.substring(0, 1).toUpperCase() + dayName.substring(1));
                }

                txtPeriod.setText("Period " + availability.getPeriod());

                txtRemark.setText(availability.getRemark());
            }
        } catch (Exception e) {
            System.err.println("Error loading availability details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) txtId.getScene().getWindow();
        if (stage != null) stage.close();
    }
}