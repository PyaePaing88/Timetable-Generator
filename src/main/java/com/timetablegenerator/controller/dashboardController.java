package com.timetablegenerator.controller;

import com.timetablegenerator.repository.classRepo;
import com.timetablegenerator.repository.courseRepo;
import com.timetablegenerator.repository.departmentRepo;
import com.timetablegenerator.repository.userRepo;
import com.timetablegenerator.service.userService;
import com.timetablegenerator.service.departmentService;
import com.timetablegenerator.service.classService;
import com.timetablegenerator.service.courseService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class dashboardController {

    @FXML
    private Label totalUsersLabel;
    @FXML
    private Label totalDeptLabel;
    @FXML
    private Label totalClassLabel;
    @FXML
    private Label totalCourseLabel;

    private final userService userService = new userService(new userRepo());
    private final departmentService deptService = new departmentService(new departmentRepo());
    private final classService classService = new classService(new classRepo());
    private final courseService courseService = new courseService(new courseRepo());

    @FXML
    public void initialize() {
        loadStatistics();
    }

    private void loadStatistics() {
        try {
            int userCount = userService.getTotalUserCount("");
            int deptCount = deptService.getTotalDepartmentCount("");
            int classCount = classService.getTotalClassCount("");
            int courseCount = courseService.getTotalCourseCount("");

            totalUsersLabel.setText(String.format("%, d", userCount));
            totalDeptLabel.setText(String.format("%, d", deptCount));
            totalClassLabel.setText(String.format("%, d", classCount));
            totalCourseLabel.setText(String.format("%, d", courseCount));

        } catch (Exception e) {
            System.err.println("Error loading dashboard statistics: " + e.getMessage());
            e.printStackTrace();
            setLabelsToError();
        }
    }

    private void setLabelsToError() {
        totalUsersLabel.setText("N/A");
        totalDeptLabel.setText("N/A");
        totalClassLabel.setText("N/A");
        totalCourseLabel.setText("N/A");
    }
}