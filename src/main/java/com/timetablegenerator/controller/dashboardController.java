package com.timetablegenerator.controller;

import com.timetablegenerator.repository.classRepo;
import com.timetablegenerator.repository.courseRepo;
import com.timetablegenerator.repository.departmentRepo;
import com.timetablegenerator.repository.userRepo;
import com.timetablegenerator.service.userService;
import com.timetablegenerator.service.departmentService;
import com.timetablegenerator.service.classService;
import com.timetablegenerator.service.courseService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;

public class dashboardController {

    @FXML
    private Label totalUsersLabel;
    @FXML
    private Label totalDeptLabel;
    @FXML
    private Label totalClassLabel;
    @FXML
    private Label totalCourseLabel;
    @FXML
    private Label monthYearLabel;
    @FXML
    private GridPane calendarGrid;

    @FXML
    private Label timeLabel;
    @FXML
    private Label amPmLabel;

    private final userService userService = new userService(new userRepo());
    private final departmentService deptService = new departmentService(new departmentRepo());
    private final classService classService = new classService(new classRepo());
    private final courseService courseService = new courseService(new courseRepo());

    @FXML
    public void initialize() {
        loadStatistics();
        calendar();
        startClock();
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

    private void calendar() {
        LocalDate now = LocalDate.now();
        monthYearLabel.setText(now.getMonth().name() + " " + now.getYear());

        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        for (int i = 0; i < 7; i++) {
            Label dayHead = new Label(daysOfWeek[i]);
            dayHead.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
            calendarGrid.add(dayHead, i, 0);
        }

        LocalDate firstOfMonth = now.withDayOfMonth(1);
        int dayOffset = firstOfMonth.getDayOfWeek().getValue() % 7;
        int daysInMonth = now.lengthOfMonth();

        for (int i = 0; i < daysInMonth; i++) {
            int date = i + 1;
            Label dayLabel = new Label(String.valueOf(date));
            dayLabel.setMinWidth(30);
            dayLabel.setAlignment(Pos.CENTER);

            if (date == now.getDayOfMonth()) {
                dayLabel.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");
            }

            int column = (i + dayOffset) % 7;
            int row = (i + dayOffset) / 7 + 1;
            calendarGrid.add(dayLabel, column, row);
        }
    }
    
    private void startClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime now = LocalTime.now();
            timeLabel.setText(String.format("%02d:%02d:%02d",
                    (now.getHour() % 12 == 0) ? 12 : now.getHour() % 12,
                    now.getMinute(),
                    now.getSecond()));
            amPmLabel.setText(now.getHour() >= 12 ? "PM" : "AM");
        }), new KeyFrame(Duration.seconds(1)));

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }
}