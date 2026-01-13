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

public class mainLayoutController {
    @FXML private StackPane contentArea;
    @FXML private VBox sidebar;
    @FXML private Label nameLabel;

    @FXML
    public void initialize() {
        userModel user = authSession.getUser();
        nameLabel.setText(user != null ? "Hello: " + user.getName() : "No user logged in");

        loadView("/view/dashboard/dashboard.fxml", null);

        sidebar.getChildren().stream()
                .filter(node -> node instanceof Button)
                .map(node -> (Button) node)
                .filter(btn -> "Dashboard".equalsIgnoreCase(btn.getText()))
                .findFirst()
                .ifPresent(btn -> {
                    btn.getStyleClass().add("active");
                    updateButtonIcon(btn, true); // Manually trigger active icon for Dashboard
                });
    }

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

    private void loadView(String fxml, javafx.event.ActionEvent event) {
        try {
            java.net.URL url = getClass().getResource(fxml);
            if (url == null) {
                System.err.println("Could not find FXML file: " + fxml);
                return;
            }

            Node view = FXMLLoader.load(url);

            contentArea.getChildren().setAll(view);

            if (event != null) {
                setActiveButton(event);
            }
        } catch (Exception e) {
            System.err.println("Error loading view: " + fxml);
            e.printStackTrace();
        }
    }


    private void setActiveButton(javafx.event.ActionEvent event) {
        // 1. Reset all buttons to normal state
        sidebar.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.getStyleClass().remove("active");
                updateButtonIcon(btn, false); // Switch to normal icon
            }
        });

        // 2. Set the clicked button to active state
        if (event != null) {
            Button clicked = (Button) event.getSource();
            clicked.getStyleClass().add("active");
            updateButtonIcon(clicked, true); // Switch to active icon
        }
    }

    private void updateButtonIcon(Button button, boolean isActive) {
        if (button.getGraphic() instanceof ImageView) {
            ImageView iv = (ImageView) button.getGraphic();
            if (iv.getImage() != null) {
                String currentUrl = iv.getImage().getUrl();

                // Swap the folder name in the path
                String newUrl;
                if (isActive) {
                    newUrl = currentUrl.replace("/normal/", "/active/");
                } else {
                    newUrl = currentUrl.replace("/active/", "/normal/");
                }

                // Update the image only if the path actually changed
                if (!currentUrl.equals(newUrl)) {
                    iv.setImage(new Image(newUrl));
                }
            }
        }
    }

    @FXML
    private void onLogout() {
        authSession.clear();
        mainApp.getInstance().showLogin();
    }
}
