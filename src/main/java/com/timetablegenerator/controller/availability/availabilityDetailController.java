package com.timetablegenerator.controller.availability;

import com.timetablegenerator.model.availabilityModel;
import com.timetablegenerator.repository.availabilityRepo;
import com.timetablegenerator.service.availabilityService;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;

public class availabilityDetailController {

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtTeacher;
    @FXML
    private TextField txtStatus;
    @FXML
    private TextField txtFrom;
    @FXML
    private TextField txtTo;
    @FXML
    private TextArea txtRemark;

    private final availabilityService service = new availabilityService(new availabilityRepo());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public void loadAvailabilityData(int id) {
        try {
            availabilityModel availability = service.getAvailabilityById(id);
            if (availability != null) {
                txtId.setText(String.valueOf(availability.getId()));

                txtTeacher.setText(availability.getTeacher_name());

                txtStatus.setText(availability.getStatus());

                if (availability.getFrom() != null) {
                    txtFrom.setText(dateFormat.format(availability.getFrom()));
                }
                if (availability.getTo() != null) {
                    txtTo.setText(dateFormat.format(availability.getTo()));
                }

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
        stage.close();
    }
}