package com.timetablegenerator.controller.timeSlot;

import com.timetablegenerator.model.timeSlotModel;
import com.timetablegenerator.repository.timeSlotRepo;
import com.timetablegenerator.service.timeSlotService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;

public class timeSlotDetailController {

    @FXML private Label dayLabel;
    @FXML private Label periodLabel;
    @FXML private Label startTimeLabel;
    @FXML private Label endTimeLabel;
    @FXML private Label idLabel;

    private final timeSlotService service;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public timeSlotDetailController() {
        this.service = new timeSlotService(new timeSlotRepo());
    }

    public void loadTimeSlotData(int id) {
        try {
            timeSlotModel slot = service.getTimeSlotById(id);
            if (slot != null) {
                idLabel.setText(String.valueOf(slot.getId()));
                dayLabel.setText(slot.getDay_of_week().toString());
                periodLabel.setText("Period " + slot.getPeriod());

                // Format the Timestamps for display
                if (slot.getStart_time() != null) {
                    startTimeLabel.setText(timeFormat.format(slot.getStart_time()));
                }
                if (slot.getEnd_time() != null) {
                    endTimeLabel.setText(timeFormat.format(slot.getEnd_time()));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading details: " + e.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) dayLabel.getScene().getWindow();
        stage.close();
    }
}