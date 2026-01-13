package com.timetablegenerator.controller;

import com.timetablegenerator.mainApp;
import com.timetablegenerator.model.userModel;
import com.timetablegenerator.util.authSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;

public class mainLayoutController {
    @FXML
    private StackPane contentArea;
    @FXML
    private VBox sidebar;
    @FXML
    private Label nameLabel;

    @FXML
    public void initialize() {
        userModel user = authSession.getUser();
        nameLabel.setText(user != null ? "Hello: " + user.getName() : "No user logged in");

        loadView("/view/dashboard/dashboard.fxml", null);

        autoSelectButton("Dashboard");
    }

    private void autoSelectButton(String buttonText) {
        sidebar.getChildren().stream()
                .filter(node -> node instanceof Button)
                .map(node -> (Button) node)
                .filter(btn -> buttonText.equalsIgnoreCase(btn.getText().trim()))
                .findFirst()
                .ifPresent(this::applyActiveStyle);
    }

    private void loadView(String fxml, javafx.event.ActionEvent event) {
        try {
            URL url = getClass().getResource(fxml);
            if (url == null) {
                System.err.println("Could not find FXML file: " + fxml);
                return;
            }

            Node view = FXMLLoader.load(url);
            contentArea.getChildren().setAll(view);

            // If triggered by a click, update styles based on the clicked button
            if (event != null && event.getSource() instanceof Button) {
                applyActiveStyle((Button) event.getSource());
            }
        } catch (Exception e) {
            System.err.println("Error loading view: " + fxml);
            e.printStackTrace();
        }
    }

    private void applyActiveStyle(Button targetButton) {
        // Reset all buttons in sidebar
        sidebar.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.getStyleClass().remove("active");
                updateButtonIcon(btn, false);
            }
        });

        // Set the target button to active
        targetButton.getStyleClass().add("active");
        updateButtonIcon(targetButton, true);
    }

    private void updateButtonIcon(Button button, boolean isActive) {
        if (button.getGraphic() instanceof ImageView) {
            ImageView iv = (ImageView) button.getGraphic();
            if (iv.getImage() != null) {
                String currentUrl = iv.getImage().getUrl();
                String newUrl;

                // Toggle between /normal/ and /active/ folders
                if (isActive) {
                    newUrl = currentUrl.replace("/normal/", "/active/");
                } else {
                    newUrl = currentUrl.replace("/active/", "/normal/");
                }

                if (!currentUrl.equals(newUrl)) {
                    iv.setImage(new Image(newUrl));
                }
            }
        }
    }

    // Navigation Methods
    @FXML
    private void showDashboard(javafx.event.ActionEvent event) {
        loadView("/view/dashboard/dashboard.fxml", event);
    }

    @FXML
    private void showDepartments(javafx.event.ActionEvent event) {
        loadView("/view/department/departmentList.fxml", event);
    }

    @FXML
    private void showUser(javafx.event.ActionEvent event) {
        loadView("/view/user/userList.fxml", event);
    }

    @FXML
    private void showClasses(javafx.event.ActionEvent event) {
        loadView("/view/class/classList.fxml", event);
    }

    @FXML
    private void showCourse(javafx.event.ActionEvent event) {
        loadView("/view/course/courseList.fxml", event);
    }

    @FXML
    private void showTimeSlot(javafx.event.ActionEvent event) {
        loadView("/view/TimeSlot/timeSlotList.fxml", event);
    }

    @FXML
    private void showAcademicLevel(javafx.event.ActionEvent event) {
        loadView("/view/academicLevel/academicLevelList.fxml", event);
    }

    @FXML
    private void onLogout() {
        authSession.clear();
        mainApp.getInstance().showLogin();
    }
}